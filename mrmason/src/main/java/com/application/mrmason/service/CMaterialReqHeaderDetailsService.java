package com.application.mrmason.service;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;

import java.time.LocalDate;
import java.util.List;

public interface CMaterialReqHeaderDetailsService {

        ResponseCMaterialReqHeaderDetailsDto addMaterialRequest(CommonMaterialRequestDto request);

        CMaterialReqHeaderDetailsResponseDTO updateMaterialRequestHeaderDetails(String cMatRequestIdLineid,
                        CMaterialReqHeaderDetailsDTO requestDTO);

        List<CMaterialReqHeaderDetailsResponseDTO> getAllMaterialRequestHeaderDetails(
                        String cMatRequestIdLineid,
                        String cMatRequestId,
                        String materialCategory,
                        String brand,
                        String itemName,
                        String itemSize,
                        Integer qty,
                        LocalDate orderDate,
                        String requestedBy,
                        LocalDate updatedDate);
}
