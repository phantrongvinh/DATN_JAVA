package com.datn.project.dto.adress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private int id;
    private String address;
    private String detail;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private String wardCode;
    private String wardName;
    private String receiverName;
    private String receiverPhone;
    private boolean isPrimary;
    
}
