package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.CustomerRegistrationRequestForSPAdmin;
import com.application.mrmason.dto.CustomerRegistrationRespForSPAdmin;
import com.application.mrmason.dto.CustomerRegistrationResponseForSPAdmin;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.enums.RegSource;

public interface CustomerRegistrationForSPAdminService {
	public CustomerRegistrationResponseForSPAdmin registerCustomer(CustomerRegistrationRequestForSPAdmin requestDto,RegSource regSource);
	public CustomerRegistrationResponseForSPAdmin updateCustomer(CustomerRegistrationRequestForSPAdmin dto,RegSource regSource);
	public Page<CustomerRegistrationRespForSPAdmin> getCustomerRegistration(String userid, String userEmail, String userMobile, String userTown, Pageable pageable) ;
}
