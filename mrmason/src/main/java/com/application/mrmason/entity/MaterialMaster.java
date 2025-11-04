package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "admin_material_master")
@Data
public class MaterialMaster {

	@Id
	@Column(name = "userId_mtCat_mtSub_brand_skuId")
	private String msCatmsSubCatmsBrandSkuId; 
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
	private String updatedBy;
	private LocalDateTime updatedDate;
	private String userId;
	private String shape;
	private String width;
	private String length;
	private String thickness;
	private String status;
	@Transient
    private String materialMasterImage1;
    @Transient
    private String materialMasterImage2;
    @Transient
    private String materialMasterImage3;
    @Transient
    private String materialMasterImage4;
    @Transient
    private String materialMasterImage5;
	

}
