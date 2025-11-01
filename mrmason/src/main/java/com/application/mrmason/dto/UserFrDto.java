package com.application.mrmason.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFrDto {

	private String frUserId;

	private String frEmail;

	private String frMobile;

	private String password;

	private String frLinkedInProfile;

	private String emailVerified;

	private String mobileVerified;

	private RegSource regSource;

	private String status;

	private String updatedBy;

	private LocalDateTime updatedDate;
	private UserType userType;
    private List<String> primarySkill;
}
