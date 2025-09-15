package com.application.mrmason.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.application.mrmason.dto.*;
import com.application.mrmason.entity.MaterialPricing;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.MaterialPricingService;

@RestController
@RequestMapping("/api")
public class MaterialPricingController {

    @Autowired
    private MaterialPricingService materialPricingService;

    @PostMapping("/add-pricing")
    public ResponseEntity<GenericResponse<List<MaterialPricing>>> createPricings(
            @RequestBody List<MaterialPricingRequestDto> dtos, @RequestParam RegSource regSource) {

        List<MaterialPricing> savedPricings = materialPricingService.savePricings(dtos, regSource);
        GenericResponse<List<MaterialPricing>> response = new GenericResponse<>("Pricing saved successfully", true, savedPricings);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update-pricing")
    public ResponseEntity<GenericResponse<List<MaterialPricing>>> updatePricings(
            @RequestBody List<MaterialPricingRequestDto> dtos, @RequestParam RegSource regSource) {

        List<MaterialPricing> updatedPricings = materialPricingService.updatePricings(dtos, regSource);
        GenericResponse<List<MaterialPricing>> response = new GenericResponse<>("Pricing updated successfully", true, updatedPricings);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-pricing")
    public ResponseEntity<ResponseGetMaterialPricingDto> getPricings(
            @RequestParam(required = false) String userIdSku,
            @RequestParam(required = false) Double mrp,
            @RequestParam(required = false) Double gst,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MaterialPricing> pricingPage = materialPricingService.get(userIdSku, mrp, gst,userId, pageable);

        ResponseGetMaterialPricingDto response = new ResponseGetMaterialPricingDto();
        response.setMessage("Pricing retrieved successfully");
        response.setStatus(true);
        response.setMaterialPricings(pricingPage.getContent());
        response.setCurrentPage(pricingPage.getNumber());
        response.setPageSize(pricingPage.getSize());
        response.setTotalElements(pricingPage.getTotalElements());
        response.setTotalPages(pricingPage.getTotalPages());

        return ResponseEntity.ok(response);
    }
}

