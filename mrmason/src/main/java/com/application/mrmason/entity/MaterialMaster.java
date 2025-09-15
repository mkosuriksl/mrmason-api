package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "material_master")
@Data
public class MaterialMaster {

	@Id
	private String userIdSku; 
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
	private String updatedBy;
	private LocalDateTime updatedDate;
	private String userId;

}
