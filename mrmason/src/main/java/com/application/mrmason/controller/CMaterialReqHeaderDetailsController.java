package com.application.mrmason.controller;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;
import com.application.mrmason.service.CMaterialReqHeaderDetailsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@RestController

public class CMaterialReqHeaderDetailsController {

    @Autowired
    private CMaterialReqHeaderDetailsService service;

    @PreAuthorize("hasAuthority('ROLE_EC') OR hasAuthority('ROLE_Developer')")
    @PostMapping("/add-material-request-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> addMaterialRequestHeaderDetails(
            @RequestBody CommonMaterialRequestDto requestDto) {

        log.info("Received material request - Category: {}, Requested By: {}, Number of Items: {}",
                requestDto.getMaterialCategory(),
                requestDto.getRequestedBy(),
                (requestDto.getMaterialRequests() != null ? requestDto.getMaterialRequests().size() : 0));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String role = null;
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_EC"))) {
            role = "Customer";
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_Developer"))) {
            role = "Service Person";
        } else {
            log.error("Unauthorized role attempting to process material request: {}", authorities);
            throw new AccessDeniedException("Unauthorized role");
        }

        log.info("Processing material request as: {}", role);

        ResponseCMaterialReqHeaderDetailsDto response = service.addMaterialRequest(requestDto);

        if (response.isStatus()) {
            log.info("Material request processed successfully.");
            return ResponseEntity.ok(response);
        } else {
            log.warn("Material request processing failed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/update-material-request-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> updateMaterialRequestHeaderDetails(
            @RequestBody CMaterialReqHeaderDetailsDTO requestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdm = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_Adm"));
        if (isAdm) {
            ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();
            response.setMessage("Admins have no access to this resource.");
            response.setStatus(false);
            response.setMaterialRequestDetailsList(null);
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

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

//    @GetMapping("/get-material-request-header-details")
//    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> getAllMaterialRequestHeaderDetails(
//            @RequestParam(required = false) String cMatRequestIdLineid,
//            @RequestParam(required = false) String cMatRequestId,
//            @RequestParam(required = false) String materialCategory,
//            @RequestParam(required = false) String brand,
//            @RequestParam(required = false) String itemName,
//            @RequestParam(required = false) String itemSize,
//            @RequestParam(required = false) Integer qty,
//            @RequestParam(required = false) LocalDate orderDate,
//            @RequestParam(required = false) String requestedBy,
//            @RequestParam(required = false) LocalDate updatedDate) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//        boolean isDeveloper = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_Developer"));
//
//        if (isDeveloper) {
//            ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();
//            response.setMessage("Service Persons have no access to this resource.");
//            response.setStatus(false);
//            response.setMaterialRequestDetailsList(null);
//            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//        }
//
//        ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();
//        try {
//            List<CMaterialReqHeaderDetailsResponseDTO> materialRequests = service.getAllMaterialRequestHeaderDetails(
//                    cMatRequestIdLineid, cMatRequestId, materialCategory, brand, itemName, itemSize, qty, orderDate,
//                    requestedBy, updatedDate);
//
//            if (materialRequests != null && !materialRequests.isEmpty()) {
//                response.setMessage("Found material request header details");
//                response.setStatus(true);
//                response.setMaterialRequestDetailsList(materialRequests);
//                log.info("Found material request header details for the given parameters.");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            } else {
//                response.setMessage("No material request header details found for the given parameters.");
//                response.setStatus(false);
//                response.setMaterialRequestDetailsList(materialRequests);
//                log.warn("No material request header details found for the given parameters.");
//                return new ResponseEntity<>(response, HttpStatus.OK);
//            }
//        } catch (Exception e) {
//            log.error("Exception while fetching material request header details: {}", e.getMessage());
//            response.setMessage("An error occurred while fetching material request header details");
//            response.setStatus(false);
//            response.setMaterialRequestDetailsList(null);
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    
    @GetMapping("/get-material-request-header-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> getAllMaterialRequestHeaderDetails(
            @RequestParam(required = false) String cMatRequestIdLineid,
            @RequestParam(required = false) String cMatRequestId,
            @RequestParam(required = false) String materialCategory,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String itemSize,
            @RequestParam(required = false) Integer qty,
            @RequestParam(required = false) LocalDate orderDate,
            @RequestParam(required = false) String requestedBy,
            @RequestParam(required = false) LocalDate updatedDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isDeveloper = authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_Developer"));

        ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();

        if (isDeveloper) {
            response.setMessage("Service Persons have no access to this resource.");
            response.setStatus(false);
            response.setMaterialRequestDetailsList(null);
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try {
            Page<CMaterialReqHeaderDetailsResponseDTO> pagedResult = service.getAllMaterialRequestHeaderDetails(
                    cMatRequestIdLineid, cMatRequestId, materialCategory, brand, itemName, itemSize, qty, orderDate,
                    requestedBy, updatedDate, page, size);

            if (!pagedResult.isEmpty()) {
                response.setMessage("Found material request header details");
                response.setStatus(true);
                response.setMaterialRequestDetailsList(pagedResult.getContent());
                response.setCurrentPage(pagedResult.getNumber());
                response.setPageSize(pagedResult.getSize());
                response.setTotalElements(pagedResult.getTotalElements());
                response.setTotalPages(pagedResult.getTotalPages());
            } else {
                response.setMessage("No material request header details found for the given parameters.");
                response.setStatus(false);
                response.setMaterialRequestDetailsList(new ArrayList<>());
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception while fetching material request header details: {}", e.getMessage());
            response.setMessage("An error occurred while fetching material request header details");
            response.setStatus(false);
            response.setMaterialRequestDetailsList(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}