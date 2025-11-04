package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UploadAdminMaterialMaster;

@Repository
public interface UploadAdminMaterialMasterRepository extends JpaRepository<UploadAdminMaterialMaster, String> {
}

