package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;

import java.util.List;

@Repository
public interface CMaterialRequestHeaderRepository extends JpaRepository<CMaterialRequestHeaderEntity, String> {

        @Query("SELECT c FROM CMaterialRequestHeaderEntity c WHERE c.materialRequestId = :materialRequestId OR c.updatedBy = :updatedBy")
        List<CMaterialRequestHeaderEntity> findByMaterialRequestIdOrUpdatedBy(
                        @Param("materialRequestId") String materialRequestId,
                        @Param("updatedBy") String updatedBy);

        CMaterialRequestHeaderEntity findByMaterialRequestId(String cMatRequestId);
}
