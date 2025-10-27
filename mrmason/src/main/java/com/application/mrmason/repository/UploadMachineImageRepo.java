package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UploadMachineImage;

@Repository
public interface UploadMachineImageRepo extends JpaRepository<UploadMachineImage, String> {

}