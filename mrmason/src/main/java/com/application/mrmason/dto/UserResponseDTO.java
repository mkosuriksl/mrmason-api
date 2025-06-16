package com.application.mrmason.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String bodSeqNo;
    private String name;
    private String businessName;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String district;
    private String state;
    private String location;
    private String registeredDate;
    private String verified;
    private String serviceCategory;
    private String status;
    private String regSource;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private String username;
    private List<String> authorities;
    private List<String> serviceType; 
}

