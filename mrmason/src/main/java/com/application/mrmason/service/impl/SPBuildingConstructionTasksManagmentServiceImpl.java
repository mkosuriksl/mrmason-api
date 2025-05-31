package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MeasureTaskDto;
import com.application.mrmason.dto.SPBuildingConstructionTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPBuildingConstructionTasksManagment;
import com.application.mrmason.entity.SPElectricalTasksManagemnt;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.SPBuildingConstructionTasksManagmentRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SPBuildingConstructionTasksManagmentService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class SPBuildingConstructionTasksManagmentServiceImpl implements SPBuildingConstructionTasksManagmentService {

    @Autowired
    private SPBuildingConstructionTasksManagmentRepository repository;

    @Autowired
    UserDAO userDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SPBuildingConstructionTasksManagment> createAdmin(RegSource regSource, SPBuildingConstructionTaskRequestDTO requestDTO) throws AccessDeniedException {
    	UserInfo userInfo = getLoggedInSPInfo(regSource);
	    if (!UserType.Developer.name().equals(userInfo.role)) {
	        throw new AccessDeniedException("Only Developer users can access this API.");
	    }

	    String userId = requestDTO.getUserId();

	    // Map to store current max count for each prefix
	    Map<String, Long> prefixCounter = new HashMap<>();

	    List<SPBuildingConstructionTasksManagment> resultList = new ArrayList<>();

	    for (SPBuildingConstructionTasksManagment task : requestDTO.getTasks()) {
	        task.setUserId(userId);
	        task.setUpdatedDate(new Date());
	        task.setUpdatedBy(userInfo.userId);

	        // Build prefix like UserID102_cons_cur001_
	        String shortServiceCategory = task.getServiceCategory() != null && task.getServiceCategory().length() > 4
	                ? task.getServiceCategory().substring(0, 4)
	                : task.getServiceCategory();

	        String prefix = userId + "_" + shortServiceCategory + "_" + task.getTaskId() + "_";

	        // Get current count from DB (only once per prefix)
	        long currentCount = prefixCounter.computeIfAbsent(prefix, k -> repository.countByPrefix(k));

	        // Increment and generate new ID
	        currentCount++;
	        prefixCounter.put(prefix, currentCount); // Update counter for next one

	        String generatedId = prefix + String.format("%04d", currentCount);
	        task.setUserIdServiceCategoryTaskId(generatedId);

	        resultList.add(task);
	    }

	    return repository.saveAll(resultList);
    }

    @Override
    public List<SPBuildingConstructionTasksManagment> updateAdmin(RegSource regSource, List<SPBuildingConstructionTasksManagment> taskList) throws AccessDeniedException {
        UserInfo userInfo = getLoggedInSPInfo(regSource);
        if (!UserType.Developer.name().equals(userInfo.role)) {
            throw new AccessDeniedException("Only Developer users can access this API.");
        }
        List<SPBuildingConstructionTasksManagment> updatedTasks = new ArrayList<>();
        for (SPBuildingConstructionTasksManagment task : taskList) {
            String taskIdKey = task.getUserIdServiceCategoryTaskId();
            if (taskIdKey == null || taskIdKey.isEmpty()) {
                throw new ResourceNotFoundException("userId is required for update.");
            }
            SPBuildingConstructionTasksManagment existingTask = repository.findById(taskIdKey)
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found for userIdServiceCategoryTaskId: " + taskIdKey));
            existingTask.setTaskName(task.getTaskName());
            existingTask.setMeasureName(task.getMeasureName());
            existingTask.setValue(task.getValue());
            existingTask.setUpdatedBy(userInfo.userId);
            existingTask.setUpdatedDate(new Date());
            updatedTasks.add(existingTask);
        }
        return repository.saveAll(updatedTasks);
    }

    private static class UserInfo {
        String userId;
        String role;
    }

    private UserInfo getLoggedInSPInfo(RegSource regSource) {
        // Implement logic to fetch logged-in SP info based on regSource
        // This is a placeholder, adapt as per your authentication logic
        UserInfo info = new UserInfo();
        info.userId = "demoUserId";
        info.role = "Developer";
        return info;
    }
    
    @Override
	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName) {
	    List<SPBuildingConstructionTasksManagment> records = repository.findByFilters(serviceCategory, taskId, taskName);

	    // Group records by task identity: serviceCategory + taskId + taskName
	    Map<String, List<SPBuildingConstructionTasksManagment>> grouped = records.stream()
	        .collect(Collectors.groupingBy(record -> 
	            record.getServiceCategory() + "|" + record.getTaskId() + "|" + record.getTaskName()
	        ));

	    List<TaskResponseDto> responseList = new ArrayList<>();

	    for (Map.Entry<String, List<SPBuildingConstructionTasksManagment>> entry : grouped.entrySet()) {
	        List<SPBuildingConstructionTasksManagment> groupRecords = entry.getValue();

	        // Get first record as a template for serviceCategory, taskId, taskName
	        SPBuildingConstructionTasksManagment first = groupRecords.get(0);

	        TaskResponseDto dto = new TaskResponseDto();
	        dto.setServiceCategory(first.getServiceCategory());
	        dto.setTaskId(first.getTaskId());
	        dto.setTaskName(first.getTaskName());

	        // Collect distinct measure names for this task
	        List<MeasureTaskDto> measures = groupRecords.stream()
	            .map(r -> {
	                MeasureTaskDto m = new MeasureTaskDto();
	                m.setMeasureName(r.getMeasureName());
	                return m;
	            })
	            .distinct()
	            .collect(Collectors.toList());

	        dto.setMeasureTasks(measures);

	        responseList.add(dto);
	    }

	    return responseList;
	}
}
