package com.example.demo.service.impl;
import com.example.demo.domain.entity.*;
import com.example.demo.dto.response.*;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.service.CloudinaryService;
import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.request.UpdateProductRequest;
import com.example.demo.service.ProductService;
import com.example.demo.util.SlugUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductImageRepository productImageRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                               CategoryRepository categoryRepository,
                               BrandRepository brandRepository,
                               CloudinaryService cloudinaryService,
                               ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.cloudinaryService = cloudinaryService;
        this.productImageRepository = productImageRepository;
    }

    @Override @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(String category, String brand,
            BigDecimal minPrice, BigDecimal maxPrice, String keyword, Pageable pageable) {
        String keywordParam = (keyword == null || keyword.isBlank()) 
            ? null 
            : "%" + keyword.toLowerCase() + "%";
        // Native query cannot handle camelCase sort — use unsorted pageable
        Pageable unsortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findWithFilters(category, brand, minPrice, maxPrice, keywordParam, unsortedPageable)
            .map(this::toResponse);
    }

    @Override @Transactional
    public ProductResponse getBySlug(String slug) {
        Product product = productRepository.findBySlugAndActiveTrue(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
        productRepository.incrementViewCount(product.getId());
        return toDetailResponse(product);
    }

    @Override @Transactional(readOnly = true)
    @Cacheable(value = "products:top-selling", key = "#limit")
    public List<ProductResponse> getTopSelling(int limit) {
        List<ProductResponse> result = new ArrayList<>();
        for (Product p : productRepository.findTopSelling(PageRequest.of(0, limit))) result.add(toResponse(p));
        return result;
    }

    @Override @Transactional(readOnly = true)
    @Cacheable(value = "products:newest", key = "#limit")
    public List<ProductResponse> getNewest(int limit) {
        List<ProductResponse> result = new ArrayList<>();
        for (Product p : productRepository.findNewest(PageRequest.of(0, limit))) result.add(toResponse(p));
        return result;
    }

    @Override @Transactional(readOnly = true)
    @Cacheable(value = "products:featured", key = "#limit")
    public List<ProductResponse> getFeatured(int limit) {
        List<ProductResponse> result = new ArrayList<>();
        for (Product p : productRepository.findByFeaturedTrueAndActiveTrue(PageRequest.of(0, limit))) result.add(toResponse(p));
        return result;
    }

    @Override @Transactional
    @CacheEvict(value = {"products:featured", "products:top-selling", "products:newest"}, allEntries = true)
    public ProductResponse create(CreateProductRequest request, List<MultipartFile> images) {
        String name = request.getName();
        String slug = SlugUtil.toSlug(name);
        if (productRepository.findBySlugAndActiveTrue(slug).isPresent())
            slug = slug + "-" + System.currentTimeMillis();

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        Brand brand = brandRepository.findById(request.getBrandId())
            .orElseThrow(() -> new ResourceNotFoundException("Brand", request.getBrandId()));

        Product product = new Product();
        product.setName(name); product.setSlug(slug);
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        product.setCategory(category); product.setBrand(brand);
        product.setFeatured(request.isFeatured());

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                if (!file.isEmpty()) {
                    Map<String, String> uploaded = cloudinaryService.uploadImage(file, "products");
                    ProductImage img = new ProductImage();
                    img.setProduct(product); img.setImageUrl(uploaded.get("url"));
                    img.setPublicId(uploaded.get("publicId")); img.setDisplayOrder(i); img.setPrimary(i == 0);
                    productImages.add(img);
                }
            }
            if (!productImages.isEmpty()) {
                product.setThumbnailUrl(productImages.get(0).getImageUrl());
                product.setImages(productImages);
            }
        }
        Product saved = productRepository.save(product);
        log.info("Product created: " + saved.getName() + " id=" + saved.getId());
        return toDetailResponse(saved);
    }

    @Override @Transactional
    @CacheEvict(value = {"products:featured", "products:top-selling", "products:newest"}, allEntries = true)
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (request.getName() != null) product.setName(request.getName());
        if (request.getShortDescription() != null) product.setShortDescription(request.getShortDescription());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBasePrice() != null) product.setBasePrice(request.getBasePrice());
        if (request.getFeatured() != null) product.setFeatured(request.getFeatured());
        return toDetailResponse(productRepository.save(product));
    }

    @Override @Transactional
    @CacheEvict(value = {"products:featured", "products:top-selling", "products:newest"}, allEntries = true)
    public ProductResponse addImages(Long id, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (images == null || images.isEmpty()) throw new BusinessException("Vui lòng chọn ít nhất một ảnh");
        int nextOrder = product.getImages().stream()
            .mapToInt(img -> img.getDisplayOrder() != null ? img.getDisplayOrder() : 0)
            .max().orElse(-1) + 1;
        for (int i = 0; i < images.size(); i++) {
            MultipartFile file = images.get(i);
            if (!file.isEmpty()) {
                Map<String, String> uploaded = cloudinaryService.uploadImage(file, "products");
                ProductImage img = new ProductImage();
                img.setProduct(product); img.setImageUrl(uploaded.get("url"));
                img.setPublicId(uploaded.get("publicId")); img.setDisplayOrder(nextOrder + i); img.setPrimary(false);
                product.getImages().add(img);
            }
        }
        if (product.getThumbnailUrl() == null || product.getThumbnailUrl().isBlank()) {
            product.getImages().stream().sorted(Comparator.comparingInt(ProductImage::getDisplayOrder))
                .findFirst().ifPresent(first -> product.setThumbnailUrl(first.getImageUrl()));
        }
        return toDetailResponse(productRepository.save(product));
    }

    @Override @Transactional
    @CacheEvict(value = {"products:featured", "products:top-selling", "products:newest"}, allEntries = true)
    public void deleteImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        ProductImage image = productImageRepository.findByIdAndProductId(imageId, productId)
            .orElseThrow(() -> new ResourceNotFoundException("Ảnh không tồn tại"));
        if (image.getPublicId() != null) cloudinaryService.deleteImage(image.getPublicId());
        product.getImages().remove(image);
        // Update thumbnail if primary image removed
        if (image.isPrimary() || product.getThumbnailUrl() != null && product.getThumbnailUrl().equals(image.getImageUrl())) {
            product.getImages().stream().sorted(Comparator.comparingInt(ProductImage::getDisplayOrder))
                .findFirst().ifPresentOrElse(
                    first -> { first.setPrimary(true); product.setThumbnailUrl(first.getImageUrl()); },
                    () -> product.setThumbnailUrl(null));
        }
        productRepository.save(product);
    }

    @Override @Transactional
    @CacheEvict(value = {"products:featured", "products:top-selling", "products:newest"}, allEntries = true)
    public void delete(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setActive(false); productRepository.save(product);
    }

    @Override @Transactional
    @CacheEvict(value = {"products:featured", "products:top-selling", "products:newest"}, allEntries = true)
    public void toggleActive(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setActive(!product.isActive()); productRepository.save(product);
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId()); r.setName(p.getName()); r.setSlug(p.getSlug());
        r.setShortDescription(p.getShortDescription()); r.setThumbnailUrl(p.getThumbnailUrl());
        r.setBasePrice(p.getBasePrice()); r.setSalePrice(p.getSalePrice());
        r.setCategoryName(p.getCategory().getName()); r.setBrandName(p.getBrand().getName());
        r.setAvgRating(p.getAvgRating()); r.setReviewCount(p.getReviewCount());
        r.setSoldCount(p.getSoldCount()); r.setFeatured(p.isFeatured()); r.setActive(p.isActive()); r.setCreatedAt(p.getCreatedAt());
        return r;
    }

    private ProductResponse toDetailResponse(Product p) {
        ProductResponse r = toResponse(p);
        r.setDescription(p.getDescription());
        List<VariantResponse> variants = new ArrayList<>();
        for (ProductVariant v : p.getVariants()) {
            if (!v.isActive()) continue;
            VariantResponse vr = new VariantResponse();
            vr.setId(v.getId()); vr.setSku(v.getSku()); vr.setRam(v.getRam());
            vr.setRom(v.getRom()); vr.setColor(v.getColor()); vr.setPrice(v.getPrice());
            vr.setSalePrice(v.getSalePrice()); vr.setEffectivePrice(v.getEffectivePrice());
            vr.setStock(v.getStock()); vr.setImageUrl(v.getImageUrl()); vr.setActive(v.isActive());
            variants.add(vr);
        }
        r.setVariants(variants);
        List<String> imageUrls = new ArrayList<>();
        p.getImages().stream().sorted(Comparator.comparingInt(ProductImage::getDisplayOrder))
            .forEach(img -> imageUrls.add(img.getImageUrl()));
        r.setImageUrls(imageUrls);
        return r;
    }
}