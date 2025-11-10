package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "admin_machine_assets_images")
public class AdminMachineAssetsImages {

	@Id
	@Column(name = "category_machine_brand_model")
	private String categoryMachineBrandModel;

	@NotBlank(message = "Category is required")
    @Column(name = "category")
    private String category;

    @NotBlank(message = "Machine ID is required")
    @Column(name = "machine_id")
    private String machineId;

    @NotBlank(message = "Brand is required")
    @Column(name = "brand")
    private String brand;

	@Column(name = "model_id")
	private String modelId;

	@NotBlank(message = "Model Name is required")
    @Column(name = "model_name")
    private String modelName;
	
	@Column(name = "sub_category")
	private String subCategory;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "image_url")
	private String imageUrl;
	

	@PrePersist
	private void prePersist() {
		this.categoryMachineBrandModel = category+"_"+machineId+"_"+brand+"_"+ modelName;
	}

}
