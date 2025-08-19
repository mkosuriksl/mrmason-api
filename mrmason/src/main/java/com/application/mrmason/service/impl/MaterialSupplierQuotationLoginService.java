package com.application.mrmason.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.repository.MaterialSupplierQuotatuionLoginDAO;

@Service
public class MaterialSupplierQuotationLoginService {

	@Autowired
	MaterialSupplierQuotatuionLoginDAO emailLoginRepo;

	@Autowired
	MaterialSupplierQuotationUserService userService;

	@Autowired
	MaterialSupplierQuotationUserDAO userDAO;

	@Autowired
	OtpGenerationServiceImpl otpService;

	public String isEmailExists(String email) {
		if (emailLoginRepo.findByEmail(email) != null) {
			return email;
		} else {

		}
		return null;
	}

	public Userdto getUserDto(String email, String mobile) {
		Optional<MaterialSupplierQuotationUser> user = Optional.of(userDAO.findByEmailOrMobile(email, mobile));
		MaterialSupplierQuotationUser userdb = user.get();

		Userdto dto = new Userdto();

		dto.setName(userdb.getName());
		dto.setMobile(userdb.getMobile());
		dto.setEmail(userdb.getEmail());
		dto.setAddress(userdb.getAddress());
		dto.setCity(userdb.getCity());
		dto.setDistrict(userdb.getDistrict());
		dto.setState(userdb.getState());
		dto.setLocation(userdb.getLocation());
		dto.setVerified(userdb.getVerified());
		dto.setUserType(String.valueOf(userdb.getUserType()));
		dto.setStatus(userdb.getStatus());
		dto.setBusinessName(userdb.getBusinessName());
		dto.setBodSeqNo(userdb.getBodSeqNo());
		dto.setRegisteredDate(userdb.getRegisteredDate());
		dto.setUpdatedDate(userdb.getUpdatedDate());
		dto.setServiceCategory(userdb.getServiceCategory());
		return dto;

	}

}
