package com.datn.project.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBirthdayDTO {
    private Integer id;
    private String fullName;
    private String email;
    private String birthDay;
}
