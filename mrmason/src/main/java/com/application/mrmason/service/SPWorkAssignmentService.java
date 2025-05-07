package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.entity.SPWorkAssignment;

public interface SPWorkAssignmentService {
    SPWorkAssignment createAssignment(SPWorkAssignment assignment);
    public List<SPWorkAssignment> getWorkers(String recId,String servicePersonId, String workerId, String updatedBy);
    SPWorkAssignment updateWorkAssignment(SPWorkAssignment updatedAssignment);
}
