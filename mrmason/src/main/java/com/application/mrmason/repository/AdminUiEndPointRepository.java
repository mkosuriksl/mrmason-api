package com.application.mrmason.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminUiEndPointEntity;
import com.application.mrmason.entity.AdminUiEndPointId;

public interface AdminUiEndPointRepository extends JpaRepository<AdminUiEndPointEntity, AdminUiEndPointId> {

    Optional<AdminUiEndPointEntity> findById(AdminUiEndPointId id);

    @Query("SELECT e FROM AdminUiEndPointEntity e WHERE e.id.systemId = :systemId")
    Optional<AdminUiEndPointEntity> findBySystemId(@Param("systemId") String systemId);

    @Query("SELECT e FROM AdminUiEndPointEntity e WHERE e.id.ipUrlToUi = :ipUrlToUi")
    Optional<AdminUiEndPointEntity> findByIpUrlToUi(@Param("ipUrlToUi") String ipUrlToUi);

    List<AdminUiEndPointEntity> findAllById_SystemId(String systemId);

    List<AdminUiEndPointEntity> findAllById_IpUrlToUi(String ipUrlToUi);

}
