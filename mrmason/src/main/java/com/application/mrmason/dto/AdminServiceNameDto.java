package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminServiceNameDto {
	private String serviceId;
	private String serviceName;
	private String addedBy;
	private String addedDate;
	private String serviceSubCategory;
	
	@Override
	public String toString() {
	    return "AdminServiceNameDto{" +
	           "serviceId='" + serviceId + '\'' +
	           ", serviceSubCat='" + serviceSubCategory + '\'' +
	           ", addedBy='" + addedBy + '\'' +
	           ", addedDate=" + addedDate +
	           ", serviceName='" + serviceName + '\'' +
	           '}';
	}

}

