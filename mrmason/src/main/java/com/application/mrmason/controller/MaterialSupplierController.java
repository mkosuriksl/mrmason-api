package com.application.mrmason.controller;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.MaterialSupplierQuotations;
import com.application.mrmason.dto.QuotationStatusUpdateRequest;
import com.application.mrmason.dto.QuotationUpdateRequest;
import com.application.mrmason.dto.ResponseGetMaterialSupplierQuotationdetailsDto;
import com.application.mrmason.dto.ResponseGetMaterialSupplierQuotationsheaderDto;
import com.application.mrmason.dto.ResponseInvoiceAndDetailsDto;
import com.application.mrmason.entity.MaterialSupplier;
import com.application.mrmason.entity.MaterialSupplierQuotationHeader;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.materialSupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
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
	@PostMapping("/add-material-supplier-quotation")
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

    @PutMapping("/update-material-supplier-quotation")
    public ResponseEntity<GenericResponse<List<MaterialSupplier>>> updateTasks(
            @RequestParam RegSource regSource, @RequestBody List<MaterialSupplier> requestList) throws AccessDeniedException {
        List<MaterialSupplier> savedTasks = materialSupplierService.updateMaterial(regSource, requestList);
        GenericResponse<List<MaterialSupplier>> response = new GenericResponse<>("Material Supplier Updated successfully",
                true, savedTasks);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/get-material-supplier-quotation-details")
	public ResponseEntity<ResponseGetMaterialSupplierQuotationdetailsDto> getMaterialSupplierDetails(
			@RequestParam(required = false) String quotationId,
			@RequestParam(required = false) String cmatRequestId,@RequestParam(required = false) String materialLineItem,
			@RequestParam(required = false) String supplierId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<MaterialSupplier> srpqPage = materialSupplierService
				.getMaterialSupplierDetails( quotationId,cmatRequestId,materialLineItem,supplierId, pageable);
		ResponseGetMaterialSupplierQuotationdetailsDto response = new ResponseGetMaterialSupplierQuotationdetailsDto();

		response.setMessage("Material Supplier Quotation details retrieved successfully.");
		response.setStatus(true);
		response.setMaterialSuppliers(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/get-material-supplier-quotation-header")
    public ResponseEntity<ResponseGetMaterialSupplierQuotationsheaderDto> getQuotationsByUserMobile(
    		@RequestParam(required = false) String cmatRequestId,
            @RequestParam(required = false) String userMobile,
            @RequestParam(required = false) String supplierId,          // <-- Added
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromQuotedDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toQuotedDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<MaterialSupplierQuotationHeader> pageData =
                materialSupplierService.getQuotationsByUserMobile(cmatRequestId,
                        userMobile, supplierId, fromQuotedDate, toQuotedDate, pageable);

        ResponseGetMaterialSupplierQuotationsheaderDto response = new ResponseGetMaterialSupplierQuotationsheaderDto();
        response.setMessage("Material supplier quotations fetched successfully.");
        response.setStatus(true);
        response.setMaterialSupplierQuotationHeaders(pageData.getContent());
        response.setCurrentPage(pageData.getNumber());
        response.setPageSize(pageData.getSize());
        response.setTotalElements(pageData.getTotalElements());
        response.setTotalPages(pageData.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-status-with-Invoiced")
    public ResponseEntity<?> updateQuotationStatuses(@RequestParam RegSource regSource,@RequestBody List<QuotationStatusUpdateRequest> updates) {
        try {
            materialSupplierService.updateQuotationStatuses(regSource,updates);
            return ResponseEntity.ok("Quotation and Invoice statuses updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating statuses: " + e.getMessage());
        }
    }
//    public String updateQuotation(@RequestParam RegSource regSource,@RequestBody QuotationUpdateRequest request) {
//    	materialSupplierService.updateQuotation(regSource,request);
//        return "Quotation and Invoice updated successfully";
//    }
    
	
    @GetMapping("/get-invoices-and-quotation-details")
    public ResponseEntity<ResponseInvoiceAndDetailsDto> getInvoicesAndDetails(
            @RequestParam(required = false) String updatedBy,
            @RequestParam(required = false) BigDecimal quotedAmount,
            @RequestParam(required = false) String cmatRequestId,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromInvoiceDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toInvoiceDate,
            @RequestParam(defaultValue = "0") int invoicePage,
            @RequestParam(defaultValue = "10") int invoiceSize,
            @RequestParam(defaultValue = "0") int supplierPage,
            @RequestParam(defaultValue = "10") int supplierSize) {

        // Create Pageable objects
        Pageable invoicePageable = PageRequest.of(invoicePage, invoiceSize);
        Pageable supplierPageable = PageRequest.of(supplierPage, supplierSize);

        ResponseInvoiceAndDetailsDto response = materialSupplierService.getInvoicesAndDetails(
                updatedBy, quotedAmount, cmatRequestId, invoiceNumber, fromInvoiceDate, toInvoiceDate,
                invoicePageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
