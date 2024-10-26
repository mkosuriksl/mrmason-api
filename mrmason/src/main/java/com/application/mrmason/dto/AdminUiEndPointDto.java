package com.application.mrmason.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUiEndPointDto {
    private String systemId;
    private String ipUrlToUi;
    private String updatedBy;
    private String oldIpUrlToUi;

}
