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
@Table(name = "upload_admin_materical_master_images")
public class UploadAdminMaterialMaster {

	@Id
	@Column(name = "skuId")
	private String skuId;
	
	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "material_master_image1")
	private String materialMasterImage1;
	
	@Column(name = "material_master_image2")
	private String materialMasterImage2;
	
	@Column(name = "material_master_image3")
	private String materialMasterImage3;
	
	@Column(name = "material_master_image4")
	private String materialMasterImage4;
	
	@Column(name = "material_master_image5")
	private String materialMasterImage5;
}
