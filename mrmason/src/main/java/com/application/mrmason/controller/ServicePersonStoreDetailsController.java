package com.application.mrmason.controller;

import java.util.List;
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
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import com.application.mrmason.service.ServicePersonStoreDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('Developer')")
public class ServicePersonStoreDetailsController {

    @Autowired
    private ServicePersonStoreDetailsService spStoreDetailsService;

    @PostMapping("/sp-add-store")
    public ResponseEntity<?> addSPStore(@RequestBody ServicePersonStoreDetailsEntity store) {
        ResponseSPStoreDto response = new ResponseSPStoreDto();
        try {
            ServicePersonStoreDetailsEntity spStore = spStoreDetailsService.addStore(store);
            if (spStore != null) {
                response.setMessage("Service Person Store details added successfully");
                response.setStatus(true);
                response.setData(spStore);
                log.info("Added SP Store successfully for store ID: {}", spStore.getStoreId());
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Store already exists or Service Person not present");
                response.setStatus(false);
                response.setData(null);
                log.warn("Store already exists or Service Person not present, unable to add store.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            response.setMessage("An error occurred while adding store");
            response.setStatus(false);
            response.setData(null);
            log.error("Failed to add SP Store: {}", e.getMessage());
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
                log.warn("Update failed, SP store with ID {} not found.", spStore.getSpUserIdStoreId());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatus(false);
            log.error("Exception during SP Store update: {}", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-sp-store-details")
    public ResponseEntity<ResponseGetSPStoreDto<ServicePersonStoreDetailsEntity>> getSPStoreDetails(
            @RequestParam(required = false) String spUserId,
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String spUserIdStoreId,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String gst,
            @RequestParam(required = false) String tradeLicense,
            @RequestParam(required = false) String updatedBy) {

        ResponseGetSPStoreDto<ServicePersonStoreDetailsEntity> response = new ResponseGetSPStoreDto<>();
        try {
            List<ServicePersonStoreDetailsEntity> spStoreDetails = spStoreDetailsService.getSPStoreDetails(
                    spUserId, storeId, spUserIdStoreId, location, gst, tradeLicense, updatedBy);

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
            @RequestParam(required = false) String spUserIdStoreId,
            @RequestParam(required = false) String storeId) {

        ResponseDeleteSPStoreDto response;

        if (spUserIdStoreId != null) {
            Optional<ServicePersonStoreDetailsEntity> storeToDelete = spStoreDetailsService.findStoreById(spUserIdStoreId);
            if (storeToDelete.isPresent()) {
                spStoreDetailsService.deleteSPStoreDetailsById(spUserIdStoreId);
                log.info("Deleted SP store details for spUserIdStoreId: {}", spUserIdStoreId);
                response = new ResponseDeleteSPStoreDto("Data deleted successfully for spUserIdStoreId", true,
                        List.of(storeToDelete.get()));
            } else {
                response = new ResponseDeleteSPStoreDto("No store found to delete for spUserIdStoreId", false,
                        List.of());
            }
        } else if (storeId != null) {
            Optional<ServicePersonStoreDetailsEntity> storeToDelete = spStoreDetailsService.findStoreByStoreId(storeId);
            if (storeToDelete.isPresent()) {
                spStoreDetailsService.deleteSPStoreDetailsById(storeToDelete.get().getSpUserIdStoreId());
                log.info("Deleted SP store details for storeId: {}", storeId);
                response = new ResponseDeleteSPStoreDto("Data deleted successfully for storeId", true,
                        List.of(storeToDelete.get()));
            } else {
                response = new ResponseDeleteSPStoreDto("No store found to delete for storeId", false, List.of());
            }
        } else {
            response = new ResponseDeleteSPStoreDto("Please provide either spUserIdStoreId or storeId.", false,
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
