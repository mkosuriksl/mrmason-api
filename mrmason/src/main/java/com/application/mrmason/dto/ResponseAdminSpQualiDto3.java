package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AdminSpQualification;

import lombok.Data;

@Data
public class ResponseAdminSpQualiDto3 {

	private String message;
	private boolean status;
	private List<AdminSpQualification> getAll;
}
