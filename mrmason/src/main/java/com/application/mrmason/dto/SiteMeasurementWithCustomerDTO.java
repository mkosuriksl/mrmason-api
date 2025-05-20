package com.application.mrmason.dto;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SiteMeasurement;

import lombok.Data;

@Data
public class SiteMeasurementWithCustomerDTO {
	private SiteMeasurement siteMeasurement;
	private CustomerSummaryDTO customerRegistration;

	 public SiteMeasurementWithCustomerDTO(SiteMeasurement siteMeasurement, CustomerRegistration customer) {
	        this.siteMeasurement = siteMeasurement;
	        if (customer != null) {
	            this.customerRegistration = new CustomerSummaryDTO(customer);
	        }
	    }
}
