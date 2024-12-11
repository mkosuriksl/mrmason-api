package com.application.mrmason.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.application.mrmason.dto.ResponseSPMembRenewHeader;
import com.application.mrmason.dto.SPMembRenewHeaderRequestDTO;
import com.application.mrmason.dto.SPMembRenewHeaderResponseDTO;
import com.application.mrmason.service.SPMembRenewHeaderService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@Slf4j
@PreAuthorize("hasAuthority('Developer')")
public class SPMembRenewHeaderController {

    @Autowired
    private SPMembRenewHeaderService service;

    @PostMapping("/add-sp-store-memb-renewal-header")
    public ResponseEntity<ResponseSPMembRenewHeader<SPMembRenewHeaderResponseDTO>> addMembershipOrder(
            @RequestBody SPMembRenewHeaderRequestDTO dto) {
        log.info("Adding new membership order with ID: {}", dto.getMembershipOrderId());
        SPMembRenewHeaderResponseDTO responseDTO = service.addMembershipOrder(dto);
        return ResponseEntity.ok(new ResponseSPMembRenewHeader<>("Order added successfully", "SUCCESS", responseDTO));
    }

    @PutMapping("/update-sp-store-memb-renewal-header")
    public ResponseEntity<ResponseSPMembRenewHeader<SPMembRenewHeaderResponseDTO>> updateMembershipOrder(
            @RequestBody SPMembRenewHeaderRequestDTO dto) {
        log.info("Updating membership order with ID: {}", dto.getMembershipOrderId());
        SPMembRenewHeaderResponseDTO responseDTO = service.updateMembershipOrder(dto.getMembershipOrderId(), dto);
        return ResponseEntity.ok(new ResponseSPMembRenewHeader<>("Order updated successfully", "SUCCESS", responseDTO));
    }

    @GetMapping("/get-sp-store-memb-renewal-headers")
    public ResponseEntity<ResponseSPMembRenewHeader<List<SPMembRenewHeaderResponseDTO>>> getAllMembershipOrders(
            String membershipOrderId, String orderPlacedBy) {
        log.info("Fetching all membership orders");
        List<SPMembRenewHeaderResponseDTO> responseDTOList = service.getAllMembershipOrders(membershipOrderId,
                orderPlacedBy);
        return ResponseEntity
                .ok(new ResponseSPMembRenewHeader<>("Orders retrieved successfully", "SUCCESS", responseDTOList));
    }

    @DeleteMapping("/del-sp-store-memb-renewal-header")
    public ResponseEntity<ResponseSPMembRenewHeader<Void>> deleteMembershipOrder(
            @RequestParam String membershipOrderId) {
        log.info("Deleting membership order with ID: {}", membershipOrderId);
        service.deleteMembershipOrder(membershipOrderId);
        return ResponseEntity.ok(new ResponseSPMembRenewHeader<>("Order deleted successfully", "SUCCESS", null));
    }
}
