package com.application.mrmason.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.WorkProgressDetails;
import com.application.mrmason.enums.RegSource;

public interface WorkProgressDetailsService {

    public WorkProgressDetails addWorkProgressDetails(WorkProgressDetails entity,RegSource regSource);

    public WorkProgressDetails updateWorkProgressDetails(WorkProgressDetails entity,RegSource regSource);

    public Page<WorkProgressDetails> get(String orderNoDate, String orderNo, String workDescription,
            String taskId, String subTaskId, Pageable pageable);

}
