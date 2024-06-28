package com.application.mrmason.controller;

import com.application.mrmason.entity.AdminAssetCategory;

import lombok.Data;

@Data
public class RequestAdminAssetCatDto {

	private String message;
	private boolean status;
	private AdminAssetCategory assetCategoryData;
}

