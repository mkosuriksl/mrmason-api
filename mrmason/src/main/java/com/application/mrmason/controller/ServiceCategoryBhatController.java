package com.application.mrmason.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.ResponceServiceBhatDto;
import com.application.mrmason.dto.ResponseListServiceCatBhatDto;
import com.application.mrmason.dto.ServiceCategoryBhatDto;
import com.application.mrmason.dto.ServiceCategoryBhatDto1;
import com.application.mrmason.entity.ServiceCategoryBhat;
import com.application.mrmason.service.ServiceCategoryBhatService;

@RestController

public class ServiceCategoryBhatController {

    @Autowired
    public ServiceCategoryBhatService categoryService;

    ResponseListServiceCatBhatDto response2 = new ResponseListServiceCatBhatDto();

    @PreAuthorize("hasAuthority('Adm')")
    @PostMapping("/addBhatServiceCategory")
    public ResponseEntity<?> addRentRequest(@RequestBody ServiceCategoryBhat service) {
        ResponceServiceBhatDto response = new ResponceServiceBhatDto();
        try {
            ServiceCategoryBhatDto data = categoryService.addServiceCategory(service);
            if (data != null) {
                response.setData(data);
                response.setMessage("Service category added successfully..");
                response.setStatus(true);
                return ResponseEntity.ok(response);
            }
            response.setMessage("A service is already present wih this sub category.!");
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }

    @PreAuthorize("hasAuthority('Adm')")
    @GetMapping("/getBhatServiceCategory")
    public ResponseEntity<ResponseListServiceCatBhatDto> getServiceCategory(@RequestParam(required = false) String id,
            @RequestParam(required = false) String serviceCategory,
            @RequestParam(required = false) String serviceSubCategory) {
        try {
            List<ServiceCategoryBhat> entity = categoryService.getServiceCategory(id, serviceCategory, serviceSubCategory);

            if (!entity.isEmpty()) {
                response2.setMessage("Service data fetched successfully.!");
                response2.setStatus(true);
                response2.setData(entity);
                return new ResponseEntity<>(response2, HttpStatus.OK);
            }
            response2.setMessage("No data found for the given details.!");
            response2.setStatus(true);
            response2.setData(entity);
            return new ResponseEntity<>(response2, HttpStatus.OK);

        } catch (Exception e) {
            response2.setMessage(e.getMessage());
            response2.setStatus(false);
            return new ResponseEntity<>(response2, HttpStatus.OK);
        }

    }

    @GetMapping("/getBhatServiceCategory/civil/{serviceCategory}")
    public ResponseEntity<ResponseListServiceCatBhatDto> getServiceCategoryCivil(@PathVariable String serviceCategory) {
        try {
            List<ServiceCategoryBhat> entity = categoryService.getServiceCategoryCivil(serviceCategory);

            response2.setMessage("Civil service data fetched successfully.!");
            response2.setStatus(true);
            response2.setData(entity);
            return new ResponseEntity<>(response2, HttpStatus.OK);

        } catch (Exception e) {
            response2.setMessage(e.getMessage());
            response2.setStatus(false);
            return new ResponseEntity<>(response2, HttpStatus.OK);
        }

    }

    @GetMapping("/getBhatServiceCategory/nonCivil/{serviceCategory}")
    public ResponseEntity<ResponseListServiceCatBhatDto> getServiceCategoryNonCivil(
            @PathVariable String serviceCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ResponseListServiceCatBhatDto response2 = new ResponseListServiceCatBhatDto();
        try {
            Page<ServiceCategoryBhat> entityPage = categoryService.getServiceCategoryNonCivil(serviceCategory, page, size);

            response2.setMessage("Non-Civil service data fetched successfully.!");
            response2.setStatus(true);
            response2.setData(entityPage.getContent());
            response2.setCurrentPage(entityPage.getNumber());
            response2.setPageSize(entityPage.getSize());
            response2.setTotalElements(entityPage.getTotalElements());
            response2.setTotalPages(entityPage.getTotalPages());

            return new ResponseEntity<>(response2, HttpStatus.OK);
        } catch (Exception e) {
            response2.setMessage(e.getMessage());
            response2.setStatus(false);
            return new ResponseEntity<>(response2, HttpStatus.OK);
        }
    }


    @PreAuthorize("hasAuthority('Adm')")
    @PutMapping("/updateBhatServiceCategory")
    public ResponseEntity<?> updateServiceCategory(@RequestBody ServiceCategoryBhat service) {
        ResponceServiceBhatDto response = new ResponceServiceBhatDto();
        try {
            ServiceCategoryBhatDto data = categoryService.updateServiceCategory(service);
            if (data != null) {

                response.setData(data);
                response.setMessage("Service category updated successfully..");
                response.setStatus(true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            response.setMessage("Invalid User.!");
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {

            response.setMessage(e.getMessage());
            response.setStatus(false);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @DeleteMapping("/deleteBhat")
    public ResponseEntity<?> deleteServiceCatRecord(@RequestParam(required = false) String id) {

        ServiceCategoryBhatDto1 response = new ServiceCategoryBhatDto1();
        try {

            ServiceCategoryBhat deleteRecord = categoryService.deleteRecord(id);
            if (deleteRecord != null) {
                response.setMessage("Deleted successfully");
                response.setStatus(true);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setMessage("Failed to delete");
                response.setStatus(false);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            response.setMessage("Error: " + e.getMessage());
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
