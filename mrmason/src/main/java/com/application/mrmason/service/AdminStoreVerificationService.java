package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.AdminStoreVerificationRequestDTO;
import com.application.mrmason.dto.AdminStoreVerificationResponse;
import com.application.mrmason.dto.AdminStoreVerificationResponseDTO;

public interface AdminStoreVerificationService {

        List<AdminStoreVerificationResponseDTO> verifyStores(List<AdminStoreVerificationRequestDTO> requestDTOs);

        AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>> getVerificationsByParams(
                        String storeId, String bodSeqNo, String bodSeqNoStoreId, String verificationStatus,
                        String updatedBy);
}
