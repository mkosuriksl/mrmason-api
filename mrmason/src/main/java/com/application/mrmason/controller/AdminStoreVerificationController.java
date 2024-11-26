package com.application.mrmason.controller;

import com.application.mrmason.dto.AdminStoreVerificationResponse;
import com.application.mrmason.dto.AdminStoreVerificationRequestDTO;
import com.application.mrmason.dto.AdminStoreVerificationResponseDTO;
import com.application.mrmason.service.AdminStoreVerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@PreAuthorize("hasAuthority('Adm')")
public class AdminStoreVerificationController {

    @Autowired
    private AdminStoreVerificationService verificationService;
    @PutMapping("/admin_store_verification")
    public ResponseEntity<AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>>> verifyStores(
            @RequestBody List<AdminStoreVerificationRequestDTO> requestDTOs) {

        List<AdminStoreVerificationResponseDTO> responseDTOs = verificationService.verifyStores(requestDTOs);

        AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>> response = new AdminStoreVerificationResponse<>(
                "Store verifications updated successfully", "SUCCESS", responseDTOs);
                 return ResponseEntity.ok(response);
    }

    @GetMapping("/get_stores_verification_status")
    public ResponseEntity<AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>>> getVerifications(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String bodSeqNo,
            @RequestParam(required = false) String bodSeqNoStoreId,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) String updatedBy) {

        log.info("Received request to fetch store verifications");

        AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>> response = verificationService
                .getVerificationsByParams(storeId, bodSeqNo, bodSeqNoStoreId, verificationStatus, updatedBy);

        log.info("Returning response: {}", response.getMessage());

        return ResponseEntity.ok(response);
    }
}
