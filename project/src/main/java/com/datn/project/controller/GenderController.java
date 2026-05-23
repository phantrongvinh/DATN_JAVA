package com.datn.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.project.service.IGenderService;

@RestController
@RequestMapping(value = "/api/v1/genders")
public class GenderController {
    

    @Autowired
    private IGenderService genderService;

    @GetMapping()
    public ResponseEntity<?> getAllGender() {
        return ResponseEntity.ok(genderService.getAllGender()).getBody();
    }
}
