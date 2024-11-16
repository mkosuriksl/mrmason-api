package com.application.mrmason.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseSPMembRenewalDetailsDto {

    private String message;
    private boolean status;
    private List<SPMembRenewDetailsResponseDTO> renewalDetailsList;
    private SPMembRenewDetailsResponseDTO data;
    
}
