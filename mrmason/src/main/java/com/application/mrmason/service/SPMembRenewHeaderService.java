package com.application.mrmason.service;

import org.springframework.data.domain.Page;

import com.application.mrmason.dto.SPMembRenewHeaderRequestDTO;
import com.application.mrmason.dto.SPMembRenewHeaderResponseDTO;

public interface SPMembRenewHeaderService {
    SPMembRenewHeaderResponseDTO addMembershipOrder(SPMembRenewHeaderRequestDTO dto);

    SPMembRenewHeaderResponseDTO updateMembershipOrder(String membershipOrderId, SPMembRenewHeaderRequestDTO dto);

//    List<SPMembRenewHeaderResponseDTO> getAllMembershipOrders(String membershipOrderId,String orderPlacedBy);

    public Page<SPMembRenewHeaderResponseDTO> getAllMembershipOrders(String membershipOrderId,
            String orderPlacedBy,
            int page, int size) ;
    void deleteMembershipOrder(String membershipOrderId);
}