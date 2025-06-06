package com.application.mrmason.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.application.mrmason.dto.PaymentSPTasksManagmentRequestDTO;
import com.application.mrmason.dto.PaymentSPTasksManagmentResponseDTO;
import com.application.mrmason.dto.ResponseGetPaymentSPTasksDto;
import com.application.mrmason.entity.PaymentSPTasksManagment;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.PaymentSPTasksManagmentService;

@RestController
@RequestMapping("/payment-sp-tasks")
public class PaymentSPTasksManagmentController {

    @Autowired
    private PaymentSPTasksManagmentService service;
    @PostMapping
    public ResponseEntity<GenericResponse<List<PaymentSPTasksManagmentResponseDTO>>> create(@RequestBody List<PaymentSPTasksManagmentRequestDTO> requestDTOList,@RequestParam(required = false) RegSource regSource)throws AccessDeniedException {
        List<PaymentSPTasksManagmentResponseDTO> response = service.create(requestDTOList,regSource);
        return ResponseEntity.ok(new GenericResponse<>("Created successfully", true, response));
    }

    @PutMapping
    public ResponseEntity<GenericResponse<List<PaymentSPTasksManagmentResponseDTO>>> update(@RequestBody List<PaymentSPTasksManagmentRequestDTO> requestDTO,RegSource regSource)throws AccessDeniedException{
        List<PaymentSPTasksManagmentResponseDTO> response = service.update(requestDTO,regSource);
        return ResponseEntity.ok(new GenericResponse<>("Updated successfully", true, response));
    }

    @GetMapping
	public ResponseEntity<ResponseGetPaymentSPTasksDto> getTask(
			@RequestParam(required = false) String requestLineId, @RequestParam(required = false) String taskName,
			@RequestParam(required = false) Integer amount, @RequestParam(required = false) Integer workPersentage,
			@RequestParam(required = false) Integer amountPersentage, @RequestParam(required = false) String dailylaborPay,
			@RequestParam(required = false) String advancedPayment,
			@RequestParam(required = false) RegSource regSource, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) throws AccessDeniedException {

		Pageable pageable = PageRequest.of(page, size);
		Page<PaymentSPTasksManagment> srpqPage = service.getPayment(requestLineId, taskName, amount, workPersentage,amountPersentage,dailylaborPay,advancedPayment,
				regSource, pageable);
		ResponseGetPaymentSPTasksDto response = new ResponseGetPaymentSPTasksDto();

		response.setMessage("Payment Task  details retrieved successfully.");
		response.setStatus(true);
		response.setPaymentSPTasksManagment(srpqPage.getContent());

		// Set pagination fields
		response.setCurrentPage(srpqPage.getNumber());
		response.setPageSize(srpqPage.getSize());
		response.setTotalElements(srpqPage.getTotalElements());
		response.setTotalPages(srpqPage.getTotalPages());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
