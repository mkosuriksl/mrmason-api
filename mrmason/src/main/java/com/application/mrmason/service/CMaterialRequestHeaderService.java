package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;

public interface CMaterialRequestHeaderService {
    CMaterialRequestHeaderEntity addMaterialRequest(CMaterialRequestHeaderEntity request);

    CMaterialRequestHeaderEntity updateMaterialRequest(String id, CMaterialRequestHeaderEntity request);

    List<CMaterialRequestHeaderEntity> getAllMaterialRequests();
}