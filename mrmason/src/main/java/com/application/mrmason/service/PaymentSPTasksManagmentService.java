package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.PaymentSPTasksManagmentRequestDTO;
import com.application.mrmason.dto.PaymentSPTasksManagmentResponseDTO;
import com.application.mrmason.entity.PaymentSPTasksManagment;
import com.application.mrmason.enums.RegSource;

public interface PaymentSPTasksManagmentService {
    public List<PaymentSPTasksManagmentResponseDTO> create(List<PaymentSPTasksManagmentRequestDTO> requestDTOList,RegSource regSource)throws AccessDeniedException;
	public List<PaymentSPTasksManagmentResponseDTO> update(List<PaymentSPTasksManagmentRequestDTO> requestDTOList,RegSource regSource)throws AccessDeniedException;
	public Page<PaymentSPTasksManagment> getPayment(String requestLineId, String taskName, Integer amount,
			Integer workPersentage,Integer  amountPersentage,String dailylaborPay,String advancedPayment,RegSource regSource, Pageable pageable) throws AccessDeniedException;
}
