package com.datn.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.datn.project.entity.Gender;
import com.datn.project.repository.IGenderRepository;

@Service
public class GenderService implements IGenderService {
    
    @Autowired
    private IGenderRepository genderRepository;

    @Override
    public ResponseEntity<?> getAllGender() {
        List<Gender> genders = genderRepository.findAll();

        if (genders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "gender not found"));
        }

        return ResponseEntity.ok(genders);
    }
}
