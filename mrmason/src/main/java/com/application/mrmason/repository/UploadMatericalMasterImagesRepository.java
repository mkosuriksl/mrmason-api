package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UploadMatericalMasterImages;

@Repository
public interface UploadMatericalMasterImagesRepository extends JpaRepository<UploadMatericalMasterImages, String> {

	List<UploadMatericalMasterImages> findBySkuIdIn(List<String> skuList);
}

