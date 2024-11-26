package com.application.mrmason.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.application.mrmason.dto.ResponseDeleteSPStoreDto;
// import com.application.mrmason.dto.ResponseSPStoreDto;
import com.application.mrmason.dto.ServicePersonStoreResponse;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;

public interface ServicePersonStoreDetailsService {

    List<ServicePersonStoreDetailsEntity> addStores(List<ServicePersonStoreDetailsEntity> stores);

    // ResponseSPStoreDto verifyStore(ServicePersonStoreDetailsEntity store);

    List<ServicePersonStoreDetailsEntity> getSPStoreDetails(String bodSeqNo, String storeId,
            String bodSeqNoStoreId, LocalDate storeExpiryDate,
            String storeCurrentPlan, String verificationStatus,
            String location, String gst, String tradeLicense,
            String updatedBy);

    ResponseDeleteSPStoreDto deleteSPStoreDetailsById(String bodSeqNoStoreId);

    List<ServicePersonStoreResponse> getDataBy(String location);

    ServicePersonStoreDetailsEntity updateStore(ServicePersonStoreDetailsEntity spStore);

    Optional<ServicePersonStoreDetailsEntity> findStoreByStoreId(String storeId);

    Optional<ServicePersonStoreDetailsEntity> findStoreById(String bodSeqNoStoreId);

}