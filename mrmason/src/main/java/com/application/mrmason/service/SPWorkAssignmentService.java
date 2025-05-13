package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.SPWorkAssignment;

public interface SPWorkAssignmentService {
    SPWorkAssignment createAssignment(SPWorkAssignment assignment);
//    public List<SPWorkAssignment> getWorkers(String recId,String servicePersonId, String workerId, String updatedBy);
    public Page<SPWorkAssignment> getWorkers(String recId, String servicePersonId, String workerId, String updatedBy, Pageable pageable);
    SPWorkAssignment updateWorkAssignment(SPWorkAssignment updatedAssignment);
}
