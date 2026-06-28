package com.datn.project.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.datn.project.dto.PromotionRequest;
import com.datn.project.dto.product.ProductFilterDTO;
import com.datn.project.dto.product.ProductRequest;
import com.datn.project.dto.user.UserFilterDTO;
import com.datn.project.service.IProductService;
import com.datn.project.service.IPromotionService;
import com.datn.project.service.IUserService;

import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/api/v1/admin")
public class AdminController {

    @Autowired
    private IProductService productService;

    @Autowired
    private IPromotionService promotionService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // phần products
    @GetMapping("/products/top5")
    public ResponseEntity<?> getTop5Product() {
        return ResponseEntity.ok(productService.getTop5Product()).getBody();
    }

    @GetMapping("/products/all")
    public ResponseEntity<?> getAllProducts(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) List<Integer> audienceIds,
            @RequestParam(required = false) List<Integer> brandIds,
            @RequestParam(required = false) List<Integer> categoryIds, @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortBy) {
        ProductFilterDTO filter = new ProductFilterDTO(audienceIds, brandIds, categoryIds, search, onSale, minPrice,
                maxPrice, sortBy);
        return ResponseEntity.ok(productService.getAllProducts(page, size, filter)).getBody();
    }

    @PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ProductRequest request = objectMapper.readValue(data, ProductRequest.class);
            return productService.createProduct(request, images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        }
    }

    @PutMapping(value = "/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestParam("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            ProductRequest request = objectMapper.readValue(data, ProductRequest.class);
            return productService.updateProduct(id, request, images);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deactivateProduct(@PathVariable int id) {
        return ResponseEntity.ok(productService.deactivateProduct(id)).getBody();
    }

    // phần khuyến mãi
    @PostMapping("/promotions")
    public ResponseEntity<?> createPromotion(@RequestBody PromotionRequest req) {
        return ResponseEntity.ok(promotionService.createPromotion(req));
    }

    // phần khách hàng
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate birthDayFrom,
            @RequestParam(required = false) LocalDate birthDayTo,
            @RequestParam(required = false) LocalDateTime createdAtFrom,
            @RequestParam(required = false) LocalDateTime createdAtTo,
            @RequestParam(required = false) String sortBy) {

        UserFilterDTO filterDTO = new UserFilterDTO(search, birthDayFrom, birthDayTo, createdAtFrom, createdAtTo,
                sortBy);

        return ResponseEntity.ok(userService.getAllUser(filterDTO, page, size)).getBody();
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(value = "id") int id) {
        return ResponseEntity.ok(userService.getUserById(id)).getBody();
    }
}
