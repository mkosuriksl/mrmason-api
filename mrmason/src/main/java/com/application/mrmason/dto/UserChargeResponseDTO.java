package com.application.mrmason.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChargeResponseDTO {

    private String location;
    private String updatedDate;
    private String updatedBy;
    private String brand;
    private String model;
    private String subcategory;
    private String bodSeqNo;
    private String name;

    private List<UserChargeChildDTO> charges = new ArrayList<>();
}

