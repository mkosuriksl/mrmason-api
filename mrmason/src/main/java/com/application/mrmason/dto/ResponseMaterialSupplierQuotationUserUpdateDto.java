package com.application.mrmason.dto;

import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.User;

import lombok.Data;
@Data
public class ResponseMaterialSupplierQuotationUserUpdateDto {
	private String message;
	private boolean status;
	private MaterialSupplierQuotationUser userData;
}
