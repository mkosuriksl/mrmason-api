package com.application.mrmason.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.AddServiceGetDto;
import com.application.mrmason.dto.AddServicesDto;
import com.application.mrmason.dto.AddServicesDto1;
import com.application.mrmason.dto.AdminServiceNameDto;
import com.application.mrmason.dto.ResponseAddServiceDto;
import com.application.mrmason.dto.ResponseAddServiceGetDto;
import com.application.mrmason.dto.ResponseServiceReportDto;
import com.application.mrmason.entity.AddServices;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.AddServiceRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.impl.AddServicesServiceIml;
import com.application.mrmason.service.impl.AdminServiceNameServiceImpl;
import com.application.mrmason.service.impl.SPAvailabilityServiceIml;
import com.application.mrmason.service.impl.UserService;

@RestController
@PreAuthorize("hasAuthority('Developer')")
public class AddServiceController {

	@Autowired
	AddServicesServiceIml service;

	@Autowired
	UserService userService;

	@Autowired
	UserDAO userDAO;

	@Autowired
	SPAvailabilityServiceIml spAvailibilityImpl;

	@Autowired
	AdminServiceNameServiceImpl adminService;

	@Autowired
	AddServiceRepo repo;

	ResponseAddServiceDto response = new ResponseAddServiceDto();

	@PostMapping("/add-service")
	public ResponseEntity<?> addService(@RequestBody AddServices add) {
		try {

			AddServicesDto1 addedService = service.addServices(add);
			if (addedService != null) {
				response.setMessage("Service added successfully");
				response.setStatus(true);
				response.setAddServicesData(addedService);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			} else {
				response.setMessage("Failed to add services");
				response.setStatus(false);
				return ResponseEntity.status(HttpStatus.OK).body(response);
			}
		} catch (Exception e) {
			response.setMessage("Record already exists");
			response.setStatus(false);
			return ResponseEntity.status(HttpStatus.OK).body(response);

		}

	}

	@PutMapping("/sp-add-services-update")
	public ResponseEntity<?> updateAddServices(@RequestBody AddServiceGetDto update) {
		try {
			String userIdServiceId = update.getUserIdServiceId();
			String serviceSubCategory = update.getServiceSubCategory();
			String bodSeqNo = update.getBodSeqNo();
			AddServices upServices = service.updateAddServiceDetails(update, userIdServiceId, serviceSubCategory,
					bodSeqNo);
			if (upServices == null) {
				response.setMessage("Inavalid UserIdServiceId.!");
				response.setStatus(false);
				return new ResponseEntity<>(response, HttpStatus.OK);

			} else {

				response.setMessage("Services updated successfully");
				response.setStatus(true);
//				response.setAddServicesData(upServices);
				return ResponseEntity.ok().body(response);
			}
		} catch (Exception e) {
			response.setStatus(false);
			response.setMessage("Invalid UserIdServiceId.!");
			return ResponseEntity.status(HttpStatus.OK).body(response);
		}

	}

//	@GetMapping("/sp-user-services-get")
//	public ResponseEntity<ResponseAddServiceGetDto> getServices(@RequestParam(required = false) String bodSeqNo,
//			@RequestParam(required = false) String serviceSubCategory,
//			@RequestParam(required = false) String userIdServiceId) {
//
//		ResponseAddServiceGetDto responseGet = new ResponseAddServiceGetDto();
//
//		try {
//			List<AddServicesDto> getService = service.getAddServicesWithServiceNames(bodSeqNo, serviceSubCategory,
//					userIdServiceId);
//
//			if (!getService.isEmpty()) {
//				List<String> serviceIds = new ArrayList<>();
//				for (AddServicesDto addServiceDto : getService) {
//					if (addServiceDto.getServiceId() != null) {
//						String[] ids = addServiceDto.getServiceId().split(",");
//						Collections.addAll(serviceIds, ids);
//					}
//				}
//
//				List<AdminServiceNameDto> serviceIdList = service.getServiceNamesByIds(serviceIds);
//
//				responseGet.setMessage("Service details fetched successfully.");
//				responseGet.setStatus(true);
//				responseGet.setGetAddServicesData(getService);
//				responseGet.setGetServiceId(serviceIdList);
//				return new ResponseEntity<>(responseGet, HttpStatus.OK);
//			} else {
//				responseGet.setMessage("No services found for the given parameters.");
//				responseGet.setStatus(true);
//				return new ResponseEntity<>(responseGet, HttpStatus.OK);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			responseGet.setMessage("Error: " + e.getMessage());
//			responseGet.setStatus(false);
//			return new ResponseEntity<>(responseGet, HttpStatus.OK);
//		}
//	}

	@GetMapping("/sp-user-services-get")
	public ResponseEntity<ResponseAddServiceGetDto> getServices(
	        @RequestParam(required = false) String bodSeqNo,
	        @RequestParam(required = false) String serviceSubCategory,
	        @RequestParam(required = false) String userIdServiceId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    ResponseAddServiceGetDto responseGet = new ResponseAddServiceGetDto();

	    try {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<AddServicesDto> pagedServices = service.getAddServicesWithServiceNames(bodSeqNo, serviceSubCategory, userIdServiceId, pageable);

	        List<String> serviceIds = new ArrayList<>();
	        for (AddServicesDto dto : pagedServices.getContent()) {
	            if (dto.getServiceId() != null) {
	                String[] ids = dto.getServiceId().split(",");
	                Collections.addAll(serviceIds, ids);
	            }
	        }

	        List<AdminServiceNameDto> serviceIdList = service.getServiceNamesByIds(serviceIds);

	        responseGet.setMessage("Service details fetched successfully.");
	        responseGet.setStatus(true);
	        responseGet.setGetAddServicesData(pagedServices.getContent());
	        responseGet.setGetServiceId(serviceIdList);
	        responseGet.setCurrentPage(pagedServices.getNumber());
	        responseGet.setPageSize(pagedServices.getSize());
	        responseGet.setTotalElements(pagedServices.getTotalElements());
	        responseGet.setTotalPages(pagedServices.getTotalPages());

	        return new ResponseEntity<>(responseGet, HttpStatus.OK);

	    } catch (Exception e) {
	        e.printStackTrace();
	        responseGet.setMessage("Error: " + e.getMessage());
	        responseGet.setStatus(false);
	        return new ResponseEntity<>(responseGet, HttpStatus.OK);
	    }
	}

	@GetMapping("/sp-user-report")
	public ResponseEntity<ResponseServiceReportDto> getService(@RequestParam(required = false) String bodSeqNo) {

		ResponseServiceReportDto serviceReport = new ResponseServiceReportDto();
		Optional<User> user = Optional.ofNullable(userDAO.findByBodSeqNo(bodSeqNo));
		try {
			if (userService.getServiceProfile(user.get().getBodSeqNo()) != null) {
				serviceReport.setRegData(userService.getServiceProfile(user.get().getEmail()));
				serviceReport.setMessage("success");
				serviceReport.setStatus(true);
				serviceReport.setServData(service.getPerson(bodSeqNo, null, null));
				serviceReport.setAvailData(spAvailibilityImpl.getAvailabilitys(bodSeqNo));
				return new ResponseEntity<>(serviceReport, HttpStatus.OK);
			}
			serviceReport.setMessage("No services found for the given parameters");
			serviceReport.setStatus(false);
			return new ResponseEntity<>(serviceReport, HttpStatus.OK);

		} catch (Exception e) {
			serviceReport.setMessage(e.getMessage());
			serviceReport.setStatus(false);
			return new ResponseEntity<>(serviceReport, HttpStatus.OK);
		}

	}
}
