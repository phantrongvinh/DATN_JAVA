package com.datn.project.dto.voucher;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BirthdayPreviewResponse {
    private String monthLabel;
    private List<UserBirthdayDTO> willCreate;
    private List<UserBirthdayDTO> alreadyCreated;

}