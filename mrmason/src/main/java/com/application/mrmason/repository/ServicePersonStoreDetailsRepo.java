package com.application.mrmason.repository;

import java.time.LocalDate;
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
            "WHERE (:bodSeqNo IS NULL OR s.bodSeqNo = :bodSeqNo) " +
            "AND (:storeId IS NULL OR s.storeId = :storeId) " +
            "AND (:bodSeqNoStoreId IS NULL OR s.bodSeqNoStoreId = :bodSeqNoStoreId) " +
            "AND (:storeExpiryDate IS NULL OR s.storeExpiryDate = :storeExpiryDate) " +
            "AND (:storeCurrentPlan IS NULL OR s.storeCurrentPlan = :storeCurrentPlan) " +
            "AND (:verificationStatus IS NULL OR s.verificationStatus = :verificationStatus) " +
            "AND (:location IS NULL OR s.location = :location) " +
            "AND (:gst IS NULL OR s.gst = :gst) " +
            "AND (:tradeLicense IS NULL OR s.tradeLicense = :tradeLicense) " +
            "AND (:updatedBy IS NULL OR s.updatedBy = :updatedBy) ")
    List<ServicePersonStoreDetailsEntity> findByDynamicQuery(
            @Param("bodSeqNo") String bodSeqNo,
            @Param("storeId") String storeId,
            @Param("bodSeqNoStoreId") String bodSeqNoStoreId,
            @Param("storeExpiryDate") LocalDate storeExpiryDate,
            @Param("storeCurrentPlan") String storeCurrentPlan,
            @Param("verificationStatus") String verificationStatus,
            @Param("location") String location,
            @Param("gst") String gst,
            @Param("tradeLicense") String tradeLicense,
            @Param("updatedBy") String updatedBy);

    List<ServicePersonStoreDetailsEntity> findByLocation(String location);

    Optional<ServicePersonStoreDetailsEntity> findStoreByStoreId(String storeId);

    Optional<ServicePersonStoreDetailsEntity> findByStoreId(String storeId);

    Optional<ServicePersonStoreDetailsEntity> findByBodSeqNoStoreId(String bodSeqNoStoreId);
}
