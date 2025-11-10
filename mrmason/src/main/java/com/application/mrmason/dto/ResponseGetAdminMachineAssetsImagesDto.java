package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminMachineAssetsImages;

import lombok.Data;

@Data
public class ResponseGetAdminMachineAssetsImagesDto {
	private String message;
    private boolean status;
    private List<AdminMachineAssetsImages> adminMachineAssetsImages;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
