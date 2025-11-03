package com.application.mrmason.dto;

import lombok.Data;

@Data
public class MaterialMasterRequestDto {
	private String msCatmsSubCatmsBrandSkuId;
	private String userId;
	private String serviceCategory;
    private String materialCategory;
    private String materialSubCategory;
    private String brand;
    private String modelNo;
    private String sku;
    private String modelName;
    private String description;
    private String image;
    private String size;
	private String shape;
	private String width;
	private String length;
	private String thickness;
	private String status;
}
