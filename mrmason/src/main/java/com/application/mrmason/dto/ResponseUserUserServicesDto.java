package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.AdminServiceName;
import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.SpServiceWithNoOfProject;
import com.application.mrmason.entity.UploadUserProfileImage;
import com.application.mrmason.entity.User;

import lombok.Data;

@Data
public class ResponseUserUserServicesDto {

	private String message;
	private boolean status;
	private List<Userdto>  userData;
	private List<SpServiceDetails> userServicesData;
	private List<AddServices> userServiceInDetail;
	private List<AdminServiceName> serviceNames;
	private List<SpServiceWithNoOfProject> noOfProjects;
	private List<UploadUserProfileImage> profilePhoto;
	private List<AdminSpVerification>adminSPVerification;
}
