package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminUiEndPoint;

import lombok.Data;

@Data
public class ResponseAdminUiEndPointDto {

	private String message;
    private boolean status;
    private List<AdminUiEndPoint> data;
}
