package com.application.mrmason.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="admin_asset_category")
public class AdminAssetCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	@Column(name="assetCategory")
	private String assetCategory;
	@Column(name="assetSubCategory")
	private String assetSubCategory;
	@Column(name="addedBy")
	private String addedBy;
	@Column(name="updatedBy")
	private String updatedBy;
	@UpdateTimestamp
	@Column(name="updatedDate")
	private String updatedDate;
	@CreationTimestamp
	@Column(name="createDate")
	private String createDate;
	@Column(name="asset_brand")
	private String assetBrand;
	@Column(name="asset_model_id")
	private String assetModelId;
	@Column(name="asset_model_name")
	private String assetModelName;
}
