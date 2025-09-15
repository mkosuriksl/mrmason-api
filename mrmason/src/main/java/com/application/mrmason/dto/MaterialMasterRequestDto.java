package com.application.mrmason.dto;

import lombok.Data;

@Data
public class MaterialMasterRequestDto {
	private String userIdSku;
	private String userId;
	private String serviceCategory;
    private String productCategory;
    private String productSubCategory;
    private String brand;
    private String model;
    private String sku;
    private String name;
    private String description;
    private String image;
    private String size;
}
