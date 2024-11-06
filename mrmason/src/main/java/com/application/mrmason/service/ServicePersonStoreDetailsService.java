package com.application.mrmason.service;

import java.util.List;
import java.util.Optional;

import com.application.mrmason.dto.ResponseDeleteSPStoreDto;
import com.application.mrmason.dto.ServicePersonStoreResponse;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;

public interface ServicePersonStoreDetailsService {

    ServicePersonStoreDetailsEntity addStore(ServicePersonStoreDetailsEntity store);

    public List<ServicePersonStoreDetailsEntity> getSPStoreDetails(String spUserId, String storeId,
            String spUserIdStoreId, String location, String gst,
            String tradeLicense, String updatedBy);

    ResponseDeleteSPStoreDto deleteSPStoreDetailsById(String spUserIdStoreId);

    List<ServicePersonStoreResponse> getDataBy(String location);

    ServicePersonStoreDetailsEntity updateStore(ServicePersonStoreDetailsEntity spStore);

    Optional<ServicePersonStoreDetailsEntity> findStoreByStoreId(String storeId);

    Optional<ServicePersonStoreDetailsEntity> findStoreById(String spUserIdStoreId);

}
