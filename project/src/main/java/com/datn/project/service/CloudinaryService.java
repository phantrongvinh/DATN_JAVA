package com.datn.project.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isBlank())
            return;
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            System.err.println("Không xóa được ảnh trên Cloudinary: " + publicId);
        }
    }

    public Map<String, String> uploadImageWithPublicId(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return Map.of(
                    "url", (String) uploadResult.get("secure_url"),
                    "publicId", (String) uploadResult.get("public_id"));
        } catch (Exception e) {
            throw new RuntimeException("Upload ảnh thất bại", e);
        }
    }

     public String uploadImage(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại", e);
        }
    }
}
