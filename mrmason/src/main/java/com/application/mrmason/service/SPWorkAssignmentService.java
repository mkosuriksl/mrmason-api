package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.enums.RegSource;

public interface SPWorkAssignmentService {
	List<SPWorkAssignment> createAssignment(SPWorkAssignment assignment,RegSource regSource);
//    public List<SPWorkAssignment> getWorkers(String recId,String servicePersonId, String workerId, String updatedBy);
    public Page<SPWorkAssignment> getWorkers(String recId, String servicePersonId, String workerId, String updatedBy,String location,String available,String fromDateOfWork,String toDateOfWork,String spId, Pageable pageable);
    SPWorkAssignment updateWorkAssignment(SPWorkAssignment updatedAssignment,RegSource regSource);
}
