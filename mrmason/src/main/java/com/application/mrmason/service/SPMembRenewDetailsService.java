package com.application.mrmason.service;

import java.time.LocalDateTime;
import java.util.List;

import com.application.mrmason.dto.ResponseSPMembRenewalDetailsDto;
import com.application.mrmason.dto.SPMembRenewDetailsRequestDTO;
import com.application.mrmason.dto.SPMembRenewDetailsResponseDTO;

public interface SPMembRenewDetailsService {

    // Add membership renewal details for a single store
    public ResponseSPMembRenewalDetailsDto addMembershipRenewalDetails(
            List<SPMembRenewDetailsRequestDTO> requestDTOList);

    // Update membership renewal details
    SPMembRenewDetailsResponseDTO updateMembershipRenewalDetails(String membershipOrderIdLineItem,
            SPMembRenewDetailsRequestDTO requestDTO);

    // Get all membership renewal details with various filters
    List<SPMembRenewDetailsResponseDTO> getAllMembershipRenewalDetails(
            String membershipOrderIdLineItem,
            String membershipOrderId,
            String storeId,
            String planId,
            Integer orderAmount,
            LocalDateTime orderDate,
            String orderPlacedBy,
            String status);

    // Delete membership renewal details
    void deleteMembershipRenewalDetails(String membershipOrderIdLineItem);
}
