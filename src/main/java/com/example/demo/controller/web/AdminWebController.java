package com.example.demo.controller.web;
import com.example.demo.domain.enums.OrderStatus;
import com.example.demo.domain.entity.Product;
import com.example.demo.repository.*;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public AdminWebController(OrderRepository orderRepository, ProductRepository productRepository,
                               UserRepository userRepository, OrderService orderService, ProductService productService,
                               CategoryRepository categoryRepository, BrandRepository brandRepository) {
        this.orderRepository = orderRepository; this.productRepository = productRepository;
        this.userRepository = userRepository; this.orderService = orderService; this.productService = productService;
        this.categoryRepository = categoryRepository; this.brandRepository = brandRepository;
    }

    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        model.addAttribute("revenueThisMonth", orderRepository.getTotalRevenue(startOfMonth, LocalDateTime.now()));
        model.addAttribute("pendingOrders", orderRepository.countByStatus(OrderStatus.PENDING));
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("recentOrders", orderService.getAllOrders(null, PageRequest.of(0, 10, Sort.by("createdAt").descending())));
        model.addAttribute("topSelling", productService.getTopSelling(5));
        model.addAttribute("pageTitle", "Dashboard Quản trị");
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String keyword,
                            @RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.unsorted());
        model.addAttribute("products", productService.getProducts(null, null, null, null, keyword, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Quản lý sản phẩm");
        return "admin/products";
    }

    @GetMapping("/products/create")
    public String createProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("pageTitle", "Thêm sản phẩm mới");
        return "admin/product-form";
    }

    @Transactional(readOnly = true)
    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        
        // Lazy initialization manually to fix "no session" error in Thymeleaf
        product.getImages().size();
        
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("pageTitle", "Sửa sản phẩm");
        return "admin/product-form";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) OrderStatus status,
                          @RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        model.addAttribute("orders", orderService.getAllOrders(status, pageable));
        model.addAttribute("status", status);
        model.addAttribute("pageTitle", "Quản lý đơn hàng");
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    @Transactional(readOnly = true)
    public String orderDetail(@PathVariable Long id, Model model) {
        com.example.demo.domain.entity.Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order Id:" + id));
        
        // lazy init items
        order.getItems().size();
        
        model.addAttribute("order", order);
        model.addAttribute("pageTitle", "Chi tiết đơn hàng " + order.getOrderCode());
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order-detail";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String keyword,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.unsorted());
        String keywordParam = (keyword == null || keyword.isBlank()) ? null : "%" + keyword.toLowerCase() + "%";
        model.addAttribute("users", userRepository.searchUsers(keywordParam, pageable));
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Quản lý người dùng");
        return "admin/users";
    }
}