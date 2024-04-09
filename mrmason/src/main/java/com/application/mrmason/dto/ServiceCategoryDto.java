package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCategoryDto {
	private long id;
	private String serviceCategory;
	private String serviceSubCategory;
	private String updatedDate;
	private String updatedBy;
	private String addedBy;
	private String createDate;
}
