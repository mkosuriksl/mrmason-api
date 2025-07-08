package com.application.mrmason.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponseListServiceRequestDto;
import com.application.mrmason.dto.ResponseListServiceRequestDto1;
import com.application.mrmason.dto.ResponseServiceReqDto;
import com.application.mrmason.dto.ServiceRequestWithCustomerDTO;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.ServiceRequest;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.service.ServiceRequestService;

@RestController
//@PreAuthorize("hasAuthority('EC')")
public class ServiceRequestController {
	@Autowired
	ServiceRequestService reqService;
	ResponseListServiceRequestDto response=new ResponseListServiceRequestDto();
	
	@Autowired
	private CustomerRegistrationRepo customerRegistrationRepo;
	
	@PostMapping("/addServiceRequest")
	public ResponseEntity<ResponseServiceReqDto> addRequest(@RequestBody ServiceRequest request){
		ResponseServiceReqDto response=new ResponseServiceReqDto();
		try {
			ServiceRequest service=reqService.addRequest(request);
			if(service!=null) {	
				response.setServiceData(service);
				response.setMessage("Service request added successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}else {
				response.setMessage("Invalid User.!");
				response.setStatus(false);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return 	new ResponseEntity<>(response,HttpStatus.OK);
		}
	} 
//	@GetMapping("/getServiceRequest")
//	public ResponseEntity<ResponseListServiceRequestDto> getRequest(
//			@RequestParam(required = false) String userId,
//	        @RequestParam(required = false) String assetId,
//	        @RequestParam(required = false) String location,
//	        @RequestParam(required = false) String serviceSubCategory,
//	        @RequestParam(required = false) String email,
//	        @RequestParam(required = false) String mobile,
//	        @RequestParam(required = false) String status,
//	        @RequestParam(required = false) String fromDate,
//	        @RequestParam(required = false) String toDate) {
//
//	    try {
//	        List<ServiceRequest> serviceReqList = reqService.getServiceReq(userId,
//	                assetId, location, serviceSubCategory,email,mobile, status, fromDate, toDate);
//
//	        if (serviceReqList.isEmpty()) {
//	            response.setMessage("No data found for the given details.!");
//	            response.setData(Collections.emptyList());
//	            response.setStatus(true);
//	            return new ResponseEntity<>(response, HttpStatus.OK);
//	        }
//
//	        List<ServiceRequestWithCustomerDTO> combinedList = new ArrayList<>();
//
//	        for (ServiceRequest req : serviceReqList) {
//	            CustomerRegistration customer = customerRegistrationRepo.findByUserid(req.getRequestedBy());
//
//	            ServiceRequestWithCustomerDTO dto = new ServiceRequestWithCustomerDTO();
//	            BeanUtils.copyProperties(req, dto);
//
//	            if (customer != null) {
//	                dto.setUserName(customer.getUsername());
//	                dto.setUserEmail(customer.getUserEmail());
//	                dto.setUserMobile(customer.getUserMobile());
//	                dto.setLocation(customer.getUserTown());
//	                dto.setUserDistrict(customer.getUserDistrict());
//	                dto.setUserState(customer.getUserState());
//	                dto.setUserPincode(customer.getUserPincode());
//	            }
//
//	            combinedList.add(dto);
//	        }
//
//	        response.setData(combinedList);
//	        response.setMessage("ServiceRequest data fetched successfully.");
//	        response.setStatus(true);
//	        return ResponseEntity.ok(response);
//
//	    } catch (Exception e) {
//	        response.setMessage("Error occurred: " + e.getMessage());
//	        response.setStatus(false);
//	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//	    }
//	}
	
	@GetMapping("/getServiceRequest")
	public ResponseEntity<ResponseListServiceRequestDto> getRequest(
	        @RequestParam(required = false) String userId,
	        @RequestParam(required = false) String assetId,
	        @RequestParam(required = false) String location,
	        @RequestParam(required = false) String serviceSubCategory,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String mobile,
	        @RequestParam(required = false) String status,
	        @RequestParam(required = false) String fromDate,
	        @RequestParam(required = false) String toDate,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
	    try {
	        Page<ServiceRequest> serviceReqPage = reqService.getServiceReq(
	            userId, assetId, location, serviceSubCategory,
	            email, mobile, status, fromDate, toDate, page, size);

	        if (serviceReqPage.isEmpty()) {
	            response.setMessage("No data found for the given details.!");
	            response.setData(Collections.emptyList());
	            response.setStatus(true);
	            return ResponseEntity.ok(response);
	        }

	        List<ServiceRequestWithCustomerDTO> combinedList = new ArrayList<>();
	        for (ServiceRequest req : serviceReqPage.getContent()) {
	            CustomerRegistration customer = customerRegistrationRepo.findByUserid(req.getRequestedBy());

	            ServiceRequestWithCustomerDTO dto = new ServiceRequestWithCustomerDTO();
	            BeanUtils.copyProperties(req, dto);

	            if (customer != null) {
	                dto.setUserName(customer.getUsername());
	                dto.setUserEmail(customer.getUserEmail());
	                dto.setUserMobile(customer.getUserMobile());
	                dto.setLocation(customer.getUserTown());
	                dto.setUserDistrict(customer.getUserDistrict());
	                dto.setUserState(customer.getUserState());
	                dto.setUserPincode(customer.getUserPincode());
	            }

	            combinedList.add(dto);
	        }

	        response.setMessage("ServiceRequest data fetched successfully.");
	        response.setStatus(true);
	        response.setData(combinedList);
	        response.setCurrentPage(page);
	        response.setPageSize(size);
	        response.setTotalElement(serviceReqPage.getTotalElements());
	        response.setTotalPages(serviceReqPage.getTotalPages());

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        response.setMessage("Error occurred: " + e.getMessage());
	        response.setStatus(false);
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


//	public ResponseEntity<ResponseListServiceRequestDto> getRequest(@RequestParam(required = false)String userId,
//																	@RequestParam(required = false)String assetId,
//																	@RequestParam(required = false)String location,
//																	@RequestParam(required = false)String serviceSubCategory,
//																	@RequestParam(required = false)String email,
//																	@RequestParam(required = false)String status,
//																	@RequestParam(required = false)String mobile,
//																	@RequestParam(required = false)String fromDate,
//																	@RequestParam(required = false)String toDate){
//		try {
//
//			List<ServiceRequest> serviceReq =reqService.getServiceReq(userId, assetId, location, serviceSubCategory, email, mobile, status, fromDate, toDate);
//			if(serviceReq.isEmpty()) {
//				response.setMessage("No data found for the given details.!");
//				response.setData(serviceReq);
//				response.setStatus(true);
//				return new ResponseEntity<>(response, HttpStatus.OK);
//			}
//			response.setData(serviceReq);
//			response.setMessage("ServiceRequest data fetched successfully..");
//			response.setStatus(true);
//			return ResponseEntity.ok(response);
//
//		}catch(Exception e) {
//			response.setMessage(e.getMessage());
//			response.setStatus(false);
//			return new ResponseEntity<>(response, HttpStatus.OK);
//		}
//	}
	@PutMapping("/updateServiceRequest")
	public ResponseEntity<ResponseServiceReqDto> updateRequest(@RequestBody ServiceRequest request){
		ResponseServiceReqDto response=new ResponseServiceReqDto();
		try {
			ServiceRequest service=reqService.updateRequest(request);
			if(service!=null) {	
				response.setServiceData(service);
				response.setMessage("Service request updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}else {
				response.setMessage("Invalid User.!");
				response.setStatus(false);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return 	new ResponseEntity<>(response,HttpStatus.OK);
		}
	}
	@PutMapping("/updateServiceStatus")
	public ResponseEntity<ResponseServiceReqDto> updateStatusRequest(@RequestBody ServiceRequest request){
		ResponseServiceReqDto response=new ResponseServiceReqDto();
		try {
			ServiceRequest service=reqService.updateStatusRequest(request);
			if(service!=null) {	
				response.setServiceData(service);
				response.setMessage("Service status updated successfully..");
				response.setStatus(true);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}else {
				response.setMessage("Invalid service ID.!");
				response.setStatus(false);
				return new ResponseEntity<>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			
			response.setMessage(e.getMessage());
			response.setStatus(false);
			return 	new ResponseEntity<>(response,HttpStatus.OK);
		}
	}
	
}
