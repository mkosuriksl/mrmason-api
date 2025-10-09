package com.application.mrmason.dto;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;

import lombok.Data;

@Data
public class OrderDetailsCustomerDto {

	private CustomerRetailerOrderHdrEntity orderHdr;
	private CustomerRegistration customer;

}