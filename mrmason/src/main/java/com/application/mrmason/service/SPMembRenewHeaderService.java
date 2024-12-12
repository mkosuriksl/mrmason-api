package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.SPMembRenewHeaderRequestDTO;
import com.application.mrmason.dto.SPMembRenewHeaderResponseDTO;

public interface SPMembRenewHeaderService {
    SPMembRenewHeaderResponseDTO addMembershipOrder(SPMembRenewHeaderRequestDTO dto);

    SPMembRenewHeaderResponseDTO updateMembershipOrder(String membershipOrderId, SPMembRenewHeaderRequestDTO dto);

    List<SPMembRenewHeaderResponseDTO> getAllMembershipOrders(String membershipOrderId,String orderPlacedBy);

    void deleteMembershipOrder(String membershipOrderId);
}