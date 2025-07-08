package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;

public interface CMaterialRequestHeaderService {
    CMaterialRequestHeaderEntity addMaterialRequest(CMaterialRequestHeaderEntity request);

    CMaterialRequestHeaderEntity updateMaterialRequest(String id, CMaterialRequestHeaderEntity request);

//    List<CMaterialRequestHeaderEntity> getAllMaterialRequests();

    Page<CMaterialRequestHeaderEntity> getAllMaterialRequests(Pageable pageable);

}