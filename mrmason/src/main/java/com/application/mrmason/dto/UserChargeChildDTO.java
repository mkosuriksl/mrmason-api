package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChargeChildDTO {

    private String serviceId;
    private int serviceCharge;
    private String serviceChargeKey;
    private String serviceName;
}
