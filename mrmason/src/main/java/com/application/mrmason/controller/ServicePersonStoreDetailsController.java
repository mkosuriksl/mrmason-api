package com.application.mrmason.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseDeleteSPStoreDto;
import com.application.mrmason.dto.ResponseSPStoreDto;
import com.application.mrmason.dto.ResponseGetSPStoreDto;
import com.application.mrmason.dto.ServicePersonStoreResponse;
import com.application.mrmason.entity.AdminStoreVerificationEntity;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import com.application.mrmason.repository.AdminStoreVerificationRepository;
import com.application.mrmason.service.ServicePersonStoreDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('Developer')")
public class ServicePersonStoreDetailsController {

    @Autowired
    private ServicePersonStoreDetailsService spStoreDetailsService;

    @Autowired
    private AdminStoreVerificationRepository adminStoreVerificationRepository;

    @PostMapping("/sp-add-stores")
    public ResponseEntity<?> addSPStores(@RequestBody List<ServicePersonStoreDetailsEntity> stores) {
        ResponseSPStoreDto response = new ResponseSPStoreDto();
        try {
            List<ServicePersonStoreDetailsEntity> spStores = spStoreDetailsService.addStores(stores);
            response.setMessage("Service Person Stores added successfully");
            response.setStatus(true);
            response.setStoresList(spStores);
            log.info("Added SP Stores successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setMessage("An error occurred while adding stores");
            response.setStatus(false);
            response.setStoresList(null);
            log.error("Failed to add SP Stores: {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sp-store-verification-status")
    public ResponseEntity<?> verifyStore(@RequestBody Map<String, String> requestBody) {
        String bodSeqNoStoreId = requestBody.get("bodSeqNoStoreId");
        ResponseSPStoreDto response = new ResponseSPStoreDto();

        try {

            Optional<ServicePersonStoreDetailsEntity> storeOptional = spStoreDetailsService
                    .findStoreById(bodSeqNoStoreId);
            if (storeOptional.isPresent()) {
                ServicePersonStoreDetailsEntity store = storeOptional.get();

                Optional<AdminStoreVerificationEntity> adminVerification = adminStoreVerificationRepository
                        .findByBodSeqNoStoreId(bodSeqNoStoreId);

                if (adminVerification.isPresent()) {
                    String verificationStatus = adminVerification.get().getVerificationStatus();

                    if ("verified".equals(verificationStatus)) {
                        store.setVerificationStatus("verified");

                        spStoreDetailsService.verifyStore(store);

                        store = spStoreDetailsService.findStoreById(bodSeqNoStoreId).orElse(null);

                        if (store != null && "verified".equalsIgnoreCase(store.getVerificationStatus())) {
                            response.setMessage("Store verified and updated successfully.");
                            response.setStatus(true);
                            response.setData(store);
                            log.info("Store verified and expiry date set for bodSeqNoStoreId: {}", bodSeqNoStoreId);
                            return new ResponseEntity<>(response, HttpStatus.OK);
                        } else {
                            log.warn("Failed to update store verification status for bodSeqNoStoreId: {}",
                                    bodSeqNoStoreId);
                            response.setMessage("Store verification update failed.");
                            response.setStatus(false);
                            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    } else {

                        store.setVerificationStatus(verificationStatus);
                        store.setStoreExpiryDate(null);

                        spStoreDetailsService.verifyStore(store);
                        response.setMessage("Store verification status updated to: " + verificationStatus);
                        response.setStatus(true);
                        response.setData(store);
                        log.info("Store verification status updated to {} for bodSeqNoStoreId: {}",
                                verificationStatus, bodSeqNoStoreId);
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }
                } else {

                    response.setMessage(
                            "Admin will update verification status through mail for store " + bodSeqNoStoreId);
                    response.setStatus(false);
                    log.warn("Admin will update status through mail for store {}", bodSeqNoStoreId);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            } else {

                response.setMessage("Store not found for bodSeqNoStoreId: " + bodSeqNoStoreId);
                response.setStatus(false);
                log.warn("No store found for verification with bodSeqNoStoreId: {}", bodSeqNoStoreId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {

            log.error("Error during store verification: {}", e.getMessage());
            response.setMessage("Store verification failed. Please try again later.");
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/sp-update-store")
    public ResponseEntity<?> updateSPStore(@RequestBody ServicePersonStoreDetailsEntity spStore) {
        ResponseSPStoreDto response = new ResponseSPStoreDto();
        try {
            ServicePersonStoreDetailsEntity updatedStore = spStoreDetailsService.updateStore(spStore);
            if (updatedStore != null) {
                response.setMessage("Updated Service Person Store successfully");
                response.setStatus(true);
                response.setData(updatedStore);
                log.info("Updated SP Store successfully for store ID: {}", updatedStore.getStoreId());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Failed to update/Please check your sp_userid_store_id");
                response.setStatus(false);
                log.warn("Update failed, SP store with ID {} not found.", spStore.getBodSeqNoStoreId());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
            log.error("Exception during SP Store update: {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('Adm', 'Developer')")
    @GetMapping("/get-sp-store-details")
    public ResponseEntity<ResponseGetSPStoreDto<ServicePersonStoreDetailsEntity>> getSPStoreDetails(
            @RequestParam(required = false) String bodSeqNo,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String bodSeqNoStoreId,
            @RequestParam(required = false) Date storeExpiryDate,
            @RequestParam(required = false) String storeCurrentPlan,
            @RequestParam(required = false) String verificationStatus,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String gst,
            @RequestParam(required = false) String tradeLicense,
            @RequestParam(required = false) String updatedBy) {

        ResponseGetSPStoreDto<ServicePersonStoreDetailsEntity> response = new ResponseGetSPStoreDto<>();
        try {
            List<ServicePersonStoreDetailsEntity> spStoreDetails = spStoreDetailsService.getSPStoreDetails(bodSeqNo,
                    storeId, bodSeqNoStoreId, storeExpiryDate,
                    storeCurrentPlan, verificationStatus, location, gst, tradeLicense, updatedBy);

            if (spStoreDetails != null && !spStoreDetails.isEmpty()) {
                response.setMessage("Found Service Person store details ");
                response.setStatus(true);
                response.setData(spStoreDetails);
                log.info("Found SP store details for given parameters.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("No details found for given parameters/check your parameters");
                response.setStatus(false);
                response.setData(spStoreDetails);
                log.warn("No SP store details found for the given parameters.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Exception in fetching SP store details: {}", e.getMessage());
            response.setMessage("An error occurred while fetching SP store details");
            response.setStatus(false);
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-sp-store")
    public ResponseEntity<ResponseDeleteSPStoreDto> deleteSPStoreDetailsById(
            @RequestParam(required = false) String bodSeqNoStoreId,
            @RequestParam(required = false) String storeId) {

        ResponseDeleteSPStoreDto response;

        if (bodSeqNoStoreId != null) {
            Optional<ServicePersonStoreDetailsEntity> storeToDelete = spStoreDetailsService
                    .findStoreById(bodSeqNoStoreId);
            if (storeToDelete.isPresent()) {
                spStoreDetailsService.deleteSPStoreDetailsById(bodSeqNoStoreId);
                log.info("Deleted SP store details for   bodSeqNoStoreId: {}", bodSeqNoStoreId);
                response = new ResponseDeleteSPStoreDto("Data deleted successfully for   bodSeqNoStoreId", true,
                        List.of(storeToDelete.get()));
            } else {
                response = new ResponseDeleteSPStoreDto("No store found to delete for   bodSeqNoStoreId", false,
                        List.of());
            }
        } else if (storeId != null) {
            Optional<ServicePersonStoreDetailsEntity> storeToDelete = spStoreDetailsService.findStoreByStoreId(storeId);
            if (storeToDelete.isPresent()) {
                spStoreDetailsService.deleteSPStoreDetailsById(storeToDelete.get().getBodSeqNoStoreId());
                log.info("Deleted SP store details for storeId: {}", storeId);
                response = new ResponseDeleteSPStoreDto("Data deleted successfully for storeId", true,
                        List.of(storeToDelete.get()));
            } else {
                response = new ResponseDeleteSPStoreDto("No store found to delete for storeId", false, List.of());
            }
        } else {
            response = new ResponseDeleteSPStoreDto("Please provide either   bodSeqNoStoreId or storeId.", false,
                    List.of());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-sp-storeByLocation")
    public ResponseEntity<ResponseGetSPStoreDto<ServicePersonStoreResponse>> getDataBy(
            @RequestParam(required = false) String location) {
        ResponseGetSPStoreDto<ServicePersonStoreResponse> response = new ResponseGetSPStoreDto<>();
        try {
            List<ServicePersonStoreResponse> responses = spStoreDetailsService.getDataBy(location);

            if (responses != null && !responses.isEmpty()) {
                response.setMessage("Service person Stores found by Location");
                response.setStatus(true);
                response.setData(responses);
                log.info("Fetched product data for location: {}", location);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("No products found for location: " + location);
                response.setStatus(false);
                response.setData(responses);
                log.warn("No products found for location: {}", location);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Exception in fetching product data: {}", e.getMessage());
            response.setMessage("An error occurred while fetching product data");
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
