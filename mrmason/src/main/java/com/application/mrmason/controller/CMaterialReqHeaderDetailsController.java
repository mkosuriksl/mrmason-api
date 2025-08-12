package com.application.mrmason.controller;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CMaterialRequestHeaderDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;
import com.application.mrmason.dto.ResponseGetAdminPopTasksManagemntDto;
import com.application.mrmason.dto.ResponseGetCMaterialRequestHeaderDto;
import com.application.mrmason.entity.AdminPopTasksManagemnt;
import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.CMaterialReqHeaderDetailsService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController

public class CMaterialReqHeaderDetailsController {

    @Autowired
    private CMaterialReqHeaderDetailsService service;
    
    @Autowired
    private CustomerRegistrationRepo customerRepo;
    
    @Autowired
    private UserDAO userRepo;
//    @PreAuthorize("hasAuthority('ROLE_EC') OR hasAuthority('ROLE_Developer')")
//    @PostMapping("/add-material-request-details")
//    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> addMaterialRequestHeaderDetails(
//            @RequestBody CommonMaterialRequestDto requestDto) {
//
//        log.info("Received material request - Category: {}, Requested By: {}, Number of Items: {}",
//                requestDto.getMaterialCategory(),
//                requestDto.getRequestedBy(),
//                (requestDto.getMaterialRequests() != null ? requestDto.getMaterialRequests().size() : 0));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//
//        String role = null;
//        if (authorities.contains(new SimpleGrantedAuthority("ROLE_EC"))) {
//            role = "Customer";
//        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_Developer"))) {
//            role = "Service Person";
//        } else {
//            log.error("Unauthorized role attempting to process material request: {}", authorities);
//            throw new AccessDeniedException("Unauthorized role");
//        }
//
//        log.info("Processing material request as: {}", role);
//
//        ResponseCMaterialReqHeaderDetailsDto response = service.addMaterialRequest(requestDto);
//
//        if (response.isStatus()) {
//            log.info("Material request processed successfully.");
//            return ResponseEntity.ok(response);
//        } else {
//            log.warn("Material request processing failed.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }
    @PreAuthorize("hasAuthority('ROLE_EC') OR hasAuthority('ROLE_Developer')")
    @PostMapping("/add-material-request-details")
    public ResponseEntity<ResponseCMaterialReqHeaderDetailsDto> addMaterialRequestHeaderDetails(
            @RequestBody CommonMaterialRequestDto requestDto) {

        log.info("Received material request - Category: {}, Requested By (before token override): {}, Number of Items: {}",
                requestDto.getMaterialCategory(),
                requestDto.getRequestedBy(),
                (requestDto.getMaterialRequests() != null ? requestDto.getMaterialRequests().size() : 0));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedEmail = authentication.getName(); // email from token
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String userIdFromDb = null;

        if (authorities.contains(new SimpleGrantedAuthority("ROLE_EC"))) {
            // Get from CustomerRegistration
            CustomerRegistration customer = customerRepo
                    .findByUserEmailAndUserType(loggedEmail, UserType.EC)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found for email: " + loggedEmail));
            userIdFromDb = customer.getUserid();
            log.info("Found EC userId from CustomerRegistration: {}", userIdFromDb);
        } 
        else if (authorities.contains(new SimpleGrantedAuthority("ROLE_Developer"))) {
            // Get from User
        	User developer = userRepo
        	        .findByEmailAndUserTypeAndRegSource(loggedEmail, UserType.Developer, RegSource.MRMASON)
        	        .orElseThrow(() -> new ResourceNotFoundException(
        	            "Developer not found for email: " + loggedEmail + " with RegSource: MRMASON"));
            userIdFromDb = developer.getBodSeqNo();
            log.info("Found Developer userId from User table: {}", userIdFromDb);
        } 
        else {
            log.error("Unauthorized role attempting to process material request: {}", authorities);
            throw new AccessDeniedException("Unauthorized role");
        }

        // Override requestedBy with ID from token lookup
        requestDto.setRequestedBy(userIdFromDb);

        log.info("Processing material request for userId: {}", userIdFromDb);

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

        boolean isDeveloper = authorities.stream().anyMatch(a -> a.getAuthority().equals("Developer"));

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

    @GetMapping("/get-material-request-header")
    public ResponseEntity<ResponseGetCMaterialRequestHeaderDto> getTask(
            @RequestParam(required = false) String materialRequestId,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerMobile,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromRequestDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toRequstDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDeliveryDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDeliveryDate,
            @RequestParam(required = false) String deliveryLocation,
            @RequestParam(required = false) RegSource regSource,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {


		Pageable pageable = PageRequest.of(page, size);
		Page<CMaterialRequestHeaderEntity> srpqPage = service.getCMaterialRequestHeader(materialRequestId, customerEmail, customerName, customerMobile,
				userId,fromRequestDate,toRequstDate,fromDeliveryDate,toDeliveryDate,deliveryLocation,regSource, pageable);
		ResponseGetCMaterialRequestHeaderDto response = new ResponseGetCMaterialRequestHeaderDto();

		response.setMessage("CMaterial Request Header data retrieved successfully.");
		response.setStatus(true);
		response.setRequestHeaderEntities(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

    @GetMapping("/get-material-request-header-and-details")
    public ResponseEntity<Map<String, Object>> getMaterialRequests(
    		@RequestParam(required = false)String userId,
            @RequestParam(required = false) String materialRequestId,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerMobile,
            @RequestParam(required = false) String deliveryLocation,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromRequestDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toRequestDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fromDeliveryDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate toDeliveryDate,
            @RequestParam(required = false) String cMatRequestIdLineid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CMaterialRequestHeaderDTO> pageData = service.getMaterialRequestsWithDetails(
        		userId,materialRequestId, customerEmail, customerName, customerMobile,
            deliveryLocation, fromRequestDate, toRequestDate,
            fromDeliveryDate, toDeliveryDate,cMatRequestIdLineid,pageable
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Material request history retrieved successfully.");
        response.put("status", true);
        response.put("CMaterialRequestHeaderEntity", pageData.getContent());
        response.put("currentPage", pageData.getNumber());
        response.put("pageSize", pageData.getSize());
        response.put("totalElements", pageData.getTotalElements());
        response.put("totalPages", pageData.getTotalPages());

        return ResponseEntity.ok(response);
    }

}