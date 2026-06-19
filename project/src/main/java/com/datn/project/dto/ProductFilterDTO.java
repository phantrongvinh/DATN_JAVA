package com.datn.project.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDTO {
    private List<Integer> brandIds;
    private List<Integer> categoryIds;
    private List<Integer> audienceIds;
    private String search;
}
