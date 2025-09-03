package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.MaterialSupplierQuotations;
import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.materialSupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/material-supplier")
@RequiredArgsConstructor
public class MaterialSupplierController {

	@Autowired
    private materialSupplierService materialSupplierService ;

//    @PostMapping("/add-quotations")
//    public ResponseEntity<GenericResponse<List<MaterialSupplier>>> saveQuotations(
//            @RequestBody List<MaterialSupplier> quotations,
//            @RequestParam RegSource regSource) {
//        GenericResponse<List<MaterialSupplier>> response =
//        		materialSupplierService.saveItems(quotations, regSource);
//        return ResponseEntity.ok(response);
//    }
//    
	@PostMapping("/add-quotations")
	public ResponseEntity<GenericResponse<List<MaterialSupplier>>> saveQuotations(
	        @RequestBody MaterialSupplierQuotations request,
	        @RequestParam RegSource regSource) {
	    GenericResponse<List<MaterialSupplier>> response =
	            materialSupplierService.saveItems(request.getQuotations(),
	                                              request.getCmatRequestId(),
	                                              request.getInvoiceNumber(),
	                                              request.getInvoiceStatus(),
	                                              request.getQuotationStatus(),
	                                              request.getInvoiceDate(),
	                                              regSource);

	    return ResponseEntity.ok(response);
	}

    @PutMapping("/update-quotations")
    public ResponseEntity<GenericResponse<List<MaterialSupplier>>> updateTasks(
            @RequestParam RegSource regSource, @RequestBody List<MaterialSupplier> requestList) throws AccessDeniedException {
        List<MaterialSupplier> savedTasks = materialSupplierService.updateMaterial(regSource, requestList);
        GenericResponse<List<MaterialSupplier>> response = new GenericResponse<>("Material Supplier Updated successfully",
                true, savedTasks);
        return ResponseEntity.ok(response);
    }
}
