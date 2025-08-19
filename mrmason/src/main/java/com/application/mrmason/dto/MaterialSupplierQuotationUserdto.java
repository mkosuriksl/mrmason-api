package com.application.mrmason.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSupplierQuotationUserdto {

	public String bodSeqNo;
	public String name;
	public String businessName;
	public String mobile;
	public String email;
	public String address;
	public String city;
	public String district;
	public String state;
	public String location;
	public String availableLocation;
	public String updatedDate;
	public String registeredDate;
	public String verified;
	public String serviceCategory;
	private String userType;
	private String status;
	private String regSource;
	private String photo;
	private List<String> serviceType;
	private String VerifiedStatus;
	private String linkedInURL;
	private String highestQualification;
}
