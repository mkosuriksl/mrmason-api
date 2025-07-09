package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MeasureTaskDto;
import com.application.mrmason.dto.SPPaintTaskRequestDTO;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.SPPaintTasksManagemnt;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.SPPaintTasksManagemntRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SPPaintTasksManagemntService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class SPPaintTasksManagemntServiceImpl implements SPPaintTasksManagemntService {

	@Autowired
	private SPPaintTasksManagemntRepository repository;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<SPPaintTasksManagemnt> createAdmin(RegSource regSource, SPPaintTaskRequestDTO requestDTO)throws AccessDeniedException {
	    UserInfo userInfo = getLoggedInSPInfo(regSource);
	    if (!UserType.Developer.name().equals(userInfo.role)) {
	        throw new AccessDeniedException("Only Developer users can access this API.");
	    }

	    String userId = requestDTO.getUserId();

	    // Map to store current max count for each prefix
	    Map<String, Long> prefixCounter = new HashMap<>();

	    List<SPPaintTasksManagemnt> resultList = new ArrayList<>();

	    for (SPPaintTasksManagemnt task : requestDTO.getTasks()) {
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
	public List<SPPaintTasksManagemnt> updateAdmin(RegSource regSource, List<SPPaintTasksManagemnt> taskList) throws AccessDeniedException {
	    UserInfo userInfo = getLoggedInSPInfo(regSource);
	    if (!UserType.Developer.name().equals(userInfo.role)) {
	        throw new AccessDeniedException("Only Developer users can access this API.");
	    }

	    List<SPPaintTasksManagemnt> updatedTasks = new ArrayList<>();

	    for (SPPaintTasksManagemnt task : taskList) {
	        String taskIdKey = task.getUserIdServiceCategoryTaskId();

	        if (taskIdKey == null || taskIdKey.isEmpty()) {
	            throw new ResourceNotFoundException("userId is required for update.");
	        }

	        // Fetch the existing entity
	        SPPaintTasksManagemnt existingTask = repository.findById(taskIdKey)
	                .orElseThrow(() -> new ResourceNotFoundException("Task not found for userIdServiceCategoryTaskId: " + taskIdKey));

	        // Only update allowed fields
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

		UserInfo(String userId, String role) {
			this.userId = userId;
			this.role = role;
		}
	}

	private UserInfo getLoggedInSPInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		String userId = null;
		String role = roleNames.get(0); // Assuming only one role

		UserType userType = UserType.valueOf(role);

		if (userType == UserType.Developer) {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		} 
		return new UserInfo(userId, role);
	}

//	@Override
//	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName,RegSource regSource) throws AccessDeniedException {
//		UserInfo userInfo = getLoggedInSPInfo(regSource);
//	    if (!UserType.Developer.name().equals(userInfo.role)) {
//	        throw new AccessDeniedException("Only Developer users can access this API.");
//	    }
//	    List<SPPaintTasksManagemnt> records = repository.findByFilters(serviceCategory, taskId, taskName);
//
//	    // Group records by task identity: serviceCategory + taskId + taskName
//	    Map<String, List<SPPaintTasksManagemnt>> grouped = records.stream()
//	        .collect(Collectors.groupingBy(record -> 
//	            record.getServiceCategory() + "|" + record.getTaskId() + "|" + record.getTaskName()
//	        ));
//
//	    List<TaskResponseDto> responseList = new ArrayList<>();
//
//	    for (Map.Entry<String, List<SPPaintTasksManagemnt>> entry : grouped.entrySet()) {
//	        List<SPPaintTasksManagemnt> groupRecords = entry.getValue();
//
//	        // Get first record as a template for serviceCategory, taskId, taskName
//	        SPPaintTasksManagemnt first = groupRecords.get(0);
//
//	        TaskResponseDto dto = new TaskResponseDto();
//	        dto.setServiceCategory(first.getServiceCategory());
//	        dto.setTaskId(first.getTaskId());
//	        dto.setTaskName(first.getTaskName());
//
//	        // Collect distinct measure names for this task
//	        List<MeasureTaskDto> measures = groupRecords.stream()
//	            .map(r -> {
//	                MeasureTaskDto m = new MeasureTaskDto();
//	                m.setMeasureName(r.getMeasureName());
//	                return m;
//	            })
//	            .distinct()
//	            .collect(Collectors.toList());
//
//	        dto.setMeasureTasks(measures);
//
//	        responseList.add(dto);
//	    }
//
//	    return responseList;
//	}
	
	@Override
	public Page<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName,
	                                            RegSource regSource, int page, int size) throws AccessDeniedException {
	    UserInfo userInfo = getLoggedInSPInfo(regSource);
	    if (!UserType.Developer.name().equals(userInfo.role)) {
	        throw new AccessDeniedException("Only Developer users can access this API.");
	    }

	    Pageable pageable = PageRequest.of(page, size);

	    Page<SPPaintTasksManagemnt> records = repository.findByFilters(serviceCategory, taskId, taskName, pageable);

	    // Grouping based on taskId
	    Map<String, List<SPPaintTasksManagemnt>> grouped = records.getContent().stream()
	            .collect(Collectors.groupingBy(r -> r.getServiceCategory() + "|" + r.getTaskId() + "|" + r.getTaskName()));

	    List<TaskResponseDto> dtoList = grouped.entrySet().stream().map(entry -> {
	        SPPaintTasksManagemnt first = entry.getValue().get(0);
	        TaskResponseDto dto = new TaskResponseDto();
	        dto.setServiceCategory(first.getServiceCategory());
	        dto.setTaskId(first.getTaskId());
	        dto.setTaskName(first.getTaskName());

	        List<MeasureTaskDto> measures = entry.getValue().stream()
	                .map(r -> new MeasureTaskDto(r.getMeasureName()))
	                .distinct()
	                .collect(Collectors.toList());

	        dto.setMeasureTasks(measures);
	        return dto;
	    }).toList();

	    // Wrap result in Page manually
	    return new PageImpl<>(dtoList, pageable, records.getTotalElements());
	}

}
