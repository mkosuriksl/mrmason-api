package com.application.mrmason.dto;

import java.math.BigDecimal;

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
    private BigDecimal size;
	private String shape;
	private BigDecimal width;
	private BigDecimal length;
	private BigDecimal thickness;
	private String status;
}
