package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminMachineAssetsImages;

@Service
public interface AdminMachineAssetsImagesRepo extends JpaRepository<AdminMachineAssetsImages, String>{

}
