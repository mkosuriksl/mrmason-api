package com.application.mrmason.dto;

import lombok.Data;

@Data
public class ResponseSPAssetDTO {
    private String message;
	private boolean status;
	private ServicePersonAssetsDTO addSPAsset;

}
