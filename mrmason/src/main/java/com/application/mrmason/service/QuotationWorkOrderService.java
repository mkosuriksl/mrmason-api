package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.dto.QuotationFullResponseDTO;
import com.application.mrmason.dto.QuotationWorkOrderRequestDTO;
import com.application.mrmason.dto.QuotationWorkOrderResponseDTO;
import com.application.mrmason.entity.QuotationWorkOrder;
import com.application.mrmason.enums.RegSource;

public interface QuotationWorkOrderService {
	public List<QuotationWorkOrderResponseDTO> create(List<QuotationWorkOrderRequestDTO> requestDTOList,
			RegSource regSource) throws AccessDeniedException;

	public List<QuotationWorkOrderResponseDTO> update(List<QuotationWorkOrderRequestDTO> requestDTOList,
			RegSource regSource) throws AccessDeniedException;
	
	public List<QuotationFullResponseDTO> getWorkOrderDetails(String quotationWorkOrder, String quotationId, Date fromDate, Date toDate);
}
