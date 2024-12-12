package com.application.mrmason.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminStoreVerificationEntity;

@Repository
public interface AdminStoreVerificationRepository extends JpaRepository<AdminStoreVerificationEntity, String> {

        Optional<AdminStoreVerificationEntity> findByStoreIdAndBodSeqNo(String storeId, String bodSeqNo);

        @Query("SELECT v FROM AdminStoreVerificationEntity v " +
                        "WHERE (:storeId IS NULL OR v.storeId = :storeId) " +
                        "AND (:bodSeqNo IS NULL OR v.bodSeqNo = :bodSeqNo) " +
                        "AND (:bodSeqNoStoreId IS NULL OR v.bodSeqNoStoreId = :bodSeqNoStoreId) " +
                        "AND (:verificationStatus IS NULL OR v.verificationStatus = :verificationStatus) " +
                        "AND (:updatedBy IS NULL OR v.updatedBy = :updatedBy)")
        List<AdminStoreVerificationEntity> findByOptionalParams(
                        @Param("storeId") String storeId,
                        @Param("bodSeqNo") String bodSeqNo,
                        @Param("bodSeqNoStoreId") String bodSeqNoStoreId,
                        @Param("verificationStatus") String verificationStatus,
                        @Param("updatedBy") String updatedBy);

        Optional<AdminStoreVerificationEntity> findByBodSeqNoStoreId(String bodSeqNoStoreId);

        Optional<AdminStoreVerificationEntity> findByBodSeqNoAndStoreId(String bodSeqNo, String storeId);
}
