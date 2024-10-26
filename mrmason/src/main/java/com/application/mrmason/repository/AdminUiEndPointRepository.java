package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminUiEndPointEntity;
import com.application.mrmason.entity.AdminUiEndPointId;

@Repository
public interface AdminUiEndPointRepository extends JpaRepository<AdminUiEndPointEntity, AdminUiEndPointId> {

    @Query("SELECT e FROM AdminUiEndPointEntity e " +
            "WHERE (:systemId IS NULL OR e.id.systemId = :systemId) " +
            "AND (:ipUrlToUi IS NULL OR e.id.ipUrlToUi = :ipUrlToUi) " +
            "AND (:updatedBy IS NULL OR e.updatedBy = :updatedBy)")
    List<AdminUiEndPointEntity> findByDynamicQuery(
            @Param("systemId") String systemId,
            @Param("ipUrlToUi") String ipUrlToUi,
            @Param("updatedBy") String updatedBy);
}
