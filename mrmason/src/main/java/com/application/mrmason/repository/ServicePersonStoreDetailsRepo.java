package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;

@Repository
public interface ServicePersonStoreDetailsRepo extends JpaRepository<ServicePersonStoreDetailsEntity, String> {

        @Query("SELECT s FROM ServicePersonStoreDetailsEntity s " +
                        "WHERE (:spUserId IS NULL OR s.spUserId = :spUserId) " +
                        "AND (:storeId IS NULL OR s.storeId = :storeId) " +
                        "AND (:spUserIdStoreId IS NULL OR s.spUserIdStoreId = :spUserIdStoreId) " +
                        "AND (:location IS NULL OR s.location = :location) " +
                        "AND (:gst IS NULL OR s.gst = :gst) " +
                        "AND (:tradeLicense IS NULL OR s.tradeLicense = :tradeLicense) " +
                        "AND (:updatedBy IS NULL OR s.updatedBy = :updatedBy)")
        List<ServicePersonStoreDetailsEntity> findByDynamicQuery(
                        @Param("spUserId") String spUserId,
                        @Param("storeId") String storeId,
                        @Param("spUserIdStoreId") String spUserIdStoreId,
                        @Param("location") String location,
                        @Param("gst") String gst,
                        @Param("tradeLicense") String tradeLicense,
                        @Param("updatedBy") String updatedBy);

        List<ServicePersonStoreDetailsEntity> findByLocation(String location);

        Optional<ServicePersonStoreDetailsEntity> findStoreByStoreId(String storeId);

        Optional<ServicePersonStoreDetailsEntity> findBySpUserIdStoreId(String spUserIdStoreId);
}