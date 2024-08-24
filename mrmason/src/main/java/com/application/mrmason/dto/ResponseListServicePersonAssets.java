package com.application.mrmason.dto;
import java.util.List;

import com.application.mrmason.entity.ServicePersonAssetsEntity;

import lombok.Data;


@Data
public class ResponseListServicePersonAssets {
    private String message;
	private boolean status;
	private List<ServicePersonAssetsEntity> data;

}
