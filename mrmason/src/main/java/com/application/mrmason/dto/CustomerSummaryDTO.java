package com.application.mrmason.dto;

import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.UserType;

import lombok.Data;

@Data
public class CustomerSummaryDTO {
    private String userId;
    private String username;
    private String email;
    private String mobileNumber;
    private UserType userType;
    private String userTown;
    private String userDistrict;
    private String userState;
    private String userPincode;
    private String regDate;
    // Constructor
    public CustomerSummaryDTO(CustomerRegistration customer) {
        this.userId = customer.getUserid();
        this.username = customer.getUsername();
        this.email = customer.getUserEmail();
        this.mobileNumber = customer.getUserMobile();
        this.userType=customer.getUserType();
        this.userTown=customer.getUserTown();
        this.userDistrict=customer.getUserDistrict();
        this.userState=customer.getUserState();
        this.userPincode=customer.getUserPincode();
        this.regDate=customer.getRegDate();
    }

    // Getters and Setters
}

