package com.application.mrmason.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminMaterialMasterResponseWithImageDto {
    private String skuId;
    private String materialCategory;
    private String materialSubCategory;
    private String brand;
    private String modelNo;
    private String modelName;
    private String shape;
    private String width;
    private String length;
    private String size;
    private String thickness;
    private String status;
    private String updatedBy;
    private Date updatedDate;
    private String materialMasterImage1;
    private String materialMasterImage2;
    private String materialMasterImage3;
    private String materialMasterImage4;
    private String materialMasterImage5;
}

