package com.datn.project.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.entity.SiteSetting;
import com.datn.project.repository.ISiteSettingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/api/v1/site-setting")
public class SiteSettingController {

    @Autowired
    private ISiteSettingRepository siteSettingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Public - FE site dùng để load theme/slides
    @GetMapping
    public ResponseEntity<?> getPublic() throws Exception {
        SiteSetting setting = siteSettingRepository.findById(1)
                .orElseGet(() -> {
                    // Tạo default nếu chưa có
                    SiteSetting def = new SiteSetting();
                    def.setId(1);
                    def.setData("{}");
                    def.setUpdatedAt(LocalDateTime.now());
                    return siteSettingRepository.save(def);
                });

        String data = setting.getData();
        if (data == null || data.equals("{}")) {
            return ResponseEntity.ok(Map.of());
        }
        return ResponseEntity.ok(objectMapper.readValue(data, Map.class));
    }
}
