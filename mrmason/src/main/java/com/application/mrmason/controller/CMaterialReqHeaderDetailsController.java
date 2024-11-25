package com.application.mrmason.controller;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;
import com.application.mrmason.service.CMaterialReqHeaderDetailsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@PreAuthorize("hasAuthority('EC')")
public class CMaterialReqHeaderDetailsController {

    @Autowired
    private CMaterialReqHeaderDetailsService service;

    @PostMapping("/add-material-request-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> addMaterialRequestHeaderDetails(
            @RequestBody CommonMaterialRequestDto requestDto) {

        log.info("Received material request with category: {}, updatedBy: {}, and {} items",
                requestDto.getMaterialCategory(), requestDto.getUpdatedBy(),
                requestDto.getMaterialRequests().size());

        ResponseCMaterialReqHeaderDetailsDto response = service.addMaterialRequest(requestDto);

        log.info("Material request header details processed successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update-material-request-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> updateMaterialRequestHeaderDetails(
            @RequestBody CMaterialReqHeaderDetailsDTO requestDTO) {

        String cMatRequestIdLineid = requestDTO.getCMatRequestIdLineid();

        if (cMatRequestIdLineid == null || cMatRequestIdLineid.isEmpty()) {
            throw new RuntimeException("cMatRequestIdLineid cannot be null or empty");
        }

        CMaterialReqHeaderDetailsResponseDTO updatedResponseDTO = service
                .updateMaterialRequestHeaderDetails(cMatRequestIdLineid, requestDTO);

        ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();
        response.setStatus(true);
        response.setMessage("Material request details updated successfully.");
        response.setMaterialRequestDetailsList(List.of(updatedResponseDTO));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-material-request-header-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> getAllMaterialRequestHeaderDetails(
            @RequestParam(required = false) String cMatRequestIdLineid,
            @RequestParam(required = false) String cMatRequestId,
            @RequestParam(required = false) String materialCategory,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemSize,
            @RequestParam(required = false) Integer qty,
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) String updatedDate) {

        ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();

        try {
            List<CMaterialReqHeaderDetailsResponseDTO> materialRequests = service.getAllMaterialRequestHeaderDetails(
                    cMatRequestIdLineid, cMatRequestId, materialCategory, brand, itemName, itemSize, qty, updatedBy,
                    updatedDate);

            if (materialRequests != null && !materialRequests.isEmpty()) {
                response.setMessage("Found material request header details");
                response.setStatus(true);
                response.setMaterialRequestDetailsList(materialRequests);
                log.info("Found material request header details for the given parameters.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("No material request header details found for the given parameters.");
                response.setStatus(false);
                response.setMaterialRequestDetailsList(materialRequests);
                log.warn("No material request header details found for the given parameters.");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("Exception while fetching material request header details: {}", e.getMessage());
            response.setMessage("An error occurred while fetching material request header details");
            response.setStatus(false);
            response.setMaterialRequestDetailsList(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
