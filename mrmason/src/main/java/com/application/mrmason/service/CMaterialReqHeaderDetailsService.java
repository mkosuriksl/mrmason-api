package com.application.mrmason.service;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CMaterialRequestHeaderDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;
import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.enums.RegSource;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

public interface CMaterialReqHeaderDetailsService {

        ResponseCMaterialReqHeaderDetailsDto addMaterialRequest(CommonMaterialRequestDto request);

        CMaterialReqHeaderDetailsResponseDTO updateMaterialRequestHeaderDetails(String cMatRequestIdLineid,
                        CMaterialReqHeaderDetailsDTO requestDTO);

//        List<CMaterialReqHeaderDetailsResponseDTO> getAllMaterialRequestHeaderDetails(
//                        String cMatRequestIdLineid,
//                        String cMatRequestId,
//                        String materialCategory,
//                        String brand,
//                        String itemName,
//                        String itemSize,
//                        Integer qty,
//                        LocalDate orderDate,
//                        String requestedBy,
//                        LocalDate updatedDate);
        public Page<CMaterialReqHeaderDetailsResponseDTO> getAllMaterialRequestHeaderDetails(
                String cMatRequestIdLineid, String cMatRequestId, String materialCategory, String brand, String itemName,
                String itemSize, Integer qty, LocalDate orderDate, String requestedBy, LocalDate updatedDate,
                int page, int size);
        
    	public Page<CMaterialRequestHeaderEntity> getCMaterialRequestHeader(String materialRequestId, String customerEmail,
    			String customerName, String customerMobile, String userId, LocalDate fromRequestDate,
    			LocalDate toRequstDate,LocalDate fromDeliveryDate,LocalDate toDeliveryDate,String deliveryLocation, RegSource regSource, Pageable pageable) throws AccessDeniedException ;
    	
    	public Page<CMaterialRequestHeaderDTO> getMaterialRequestsWithDetails(
                String materialRequestId, String customerEmail, String customerName, String customerMobile,
                String deliveryLocation, LocalDate fromRequestDate, LocalDate toRequestDate,
                LocalDate fromDeliveryDate, LocalDate toDeliveryDate,String cMatRequestIdLineid, Pageable pageable);
}
