package com.application.mrmason.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.application.mrmason.dto.ResponseSPMembRenewalDetailsDto;
import com.application.mrmason.dto.SPMembRenewDetailsRequestDTO;
import com.application.mrmason.dto.SPMembRenewDetailsResponseDTO;
import com.application.mrmason.service.SPMembRenewDetailsService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('Developer')")
public class SPMembRenewDetailsController {

    @Autowired
    private SPMembRenewDetailsService service;

    @PostMapping("/add-sp-store-memb-renewal-details")
    public ResponseEntity<ResponseSPMembRenewalDetailsDto> addMembershipRenewalDetails(
            @RequestBody List<SPMembRenewDetailsRequestDTO> requestDTOList) {

        log.info("Received request with {} renewal details", requestDTOList.size());
        ResponseSPMembRenewalDetailsDto response = service.addMembershipRenewalDetails(requestDTOList);

        if (response.isStatus()) {
            log.info("Membership renewal details processed successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            log.warn("Failed to process some membership renewal details.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-sp-store-memb-renewal-details")
    public ResponseEntity<ResponseSPMembRenewalDetailsDto> updateMembershipRenewalDetails(
            @RequestBody SPMembRenewDetailsRequestDTO requestDTO) {

        ResponseSPMembRenewalDetailsDto response = new ResponseSPMembRenewalDetailsDto();

        try {
            String membershipOrderIdLineItem = requestDTO.getMembershipOrderIdLineItem();
            SPMembRenewDetailsResponseDTO updatedDetails = service
                    .updateMembershipRenewalDetails(membershipOrderIdLineItem, requestDTO);

            if (updatedDetails != null) {
                response.setMessage("Membership renewal ID " + membershipOrderIdLineItem + " updated successfully.");
                response.setStatus(true);
                response.setData(updatedDetails);
                log.info("Updated membership renewal details successfully for ID: {}", membershipOrderIdLineItem);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Failed to update. ID not found.");
                response.setStatus(false);
                log.warn("Update failed, membership renewal with ID {} not found.", membershipOrderIdLineItem);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Exception during membership renewal update: {}", e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-sp-store-memb-renewal-details")
    public ResponseEntity<ResponseSPMembRenewalDetailsDto> getMembershipRenewalDetails(
            @RequestParam(required = false) String membershipOrderIdLineItem,
            @RequestParam(required = false) String membershipOrderId,
            @RequestParam(required = false) Integer orderAmount,
            @RequestParam(required = false) LocalDateTime orderDate,
            @RequestParam(required = false) String orderPlacedBy,
            @RequestParam(required = false) String planId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String storeId) {

        ResponseSPMembRenewalDetailsDto response = new ResponseSPMembRenewalDetailsDto();

        try {
            List<SPMembRenewDetailsResponseDTO> renewals = service.getAllMembershipRenewalDetails(
                    membershipOrderIdLineItem, membershipOrderId, storeId, planId, orderAmount, orderDate,
                    orderPlacedBy, status);

            if (renewals != null && !renewals.isEmpty()) {
                response.setMessage("Found membership renewal details");
                response.setStatus(true);
                response.setRenewalDetailsList(renewals);
                log.info("Found membership renewal details for the given parameters.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("No membership renewal details found for the given parameters.");
                response.setStatus(false);
                response.setRenewalDetailsList(renewals);
                log.warn("No membership renewal details found for the given parameters.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Exception while fetching membership renewal details: {}", e.getMessage());
            response.setMessage("An error occurred while fetching membership renewal details");
            response.setStatus(false);
            response.setRenewalDetailsList(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-sp-store-memb-renewal-details")
    public ResponseEntity<ResponseSPMembRenewalDetailsDto> deleteMembershipRenewalDetails(
            @RequestParam String membershipOrderIdLineItem) {
        ResponseSPMembRenewalDetailsDto response = new ResponseSPMembRenewalDetailsDto();

        try {
            service.deleteMembershipRenewalDetails(membershipOrderIdLineItem);

            response.setMessage("Membership renewal detail deleted successfully");
            response.setStatus(true);
            response.setData(null);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error while deleting membership renewal detail: {}", e.getMessage());

            response.setMessage("membershipOrderIdLineItem not found to delete membership renewal detail");
            response.setStatus(false);
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
