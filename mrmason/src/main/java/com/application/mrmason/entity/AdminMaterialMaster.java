package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_material_master")
public class AdminMaterialMaster {

	@Id
	@Column(name = "skuId")
	private String skuId;

	@Column(name = "materical_category")
	private String materialCategory;

	@Column(name = "material_sub_category")
	private String materialSubCategory;

	@Column(name = "brand")
	private String brand;
	
	@Column(name = "model_no")
	private String modelNo;
	
	@Column(name = "model_name")
	private String modelName;
	
	@Column(name = "shape")
	private String shape;
	
	@Column(name = "width")
	private String width;
	
	@Column(name = "length")
	private String length;
	
	@Column(name = "size")
	private String size;
	
	@Column(name = "thickness")
	private String thickness;
	
	@Column(name = "status")
	private String status;
	
	
	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;
	
}
