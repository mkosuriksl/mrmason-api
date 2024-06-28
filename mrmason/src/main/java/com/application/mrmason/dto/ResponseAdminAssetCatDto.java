package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminAssetCategory;

import lombok.Data;

@Data
public class ResponseAdminAssetCatDto {
	private String message;
	private boolean status;
	private List<AdminAssetCategory> data;
}
