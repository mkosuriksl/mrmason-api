package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.MaterialRequirementByRequest;

import lombok.Data;
@Data
public class ResponsesGetMaterialRequirementByRequestDto {
	private String message;
	private boolean status;
	private List<MaterialRequirementByRequest> materialData;
	private Userdto userData;
	private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}