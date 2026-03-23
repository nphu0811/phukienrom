package com.example.demo.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CloudinaryService {
    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) { this.cloudinary = cloudinary; }

    public Map<String, String> uploadImage(MultipartFile file, String folder) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "phukienrom/" + folder, "resource_type", "image"));
            return Map.of("url", result.get("secure_url").toString(),
                          "publicId", result.get("public_id").toString());
        } catch (IOException e) {
            log.warn("Cloudinary upload failed: " + e.getMessage());
            throw new BusinessException("Upload ảnh thất bại: " + e.getMessage());
        }
    }

    public void deleteImage(String publicId) {
        try { cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()); }
        catch (IOException e) { log.warn("Cloudinary delete failed: " + e.getMessage()); }
    }
}
