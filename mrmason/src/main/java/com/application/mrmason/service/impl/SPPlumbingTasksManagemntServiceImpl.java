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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.SPPlumbingTaskRequestDTO;
import com.application.mrmason.entity.SPPlumbingTasksManagemnt;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.SPPlumbingTasksManagemntRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.SPPlumbingTasksManagemntService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class SPPlumbingTasksManagemntServiceImpl implements SPPlumbingTasksManagemntService {

	@Autowired
	private SPPlumbingTasksManagemntRepository repository;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<SPPlumbingTasksManagemnt> createAdmin(RegSource regSource, SPPlumbingTaskRequestDTO requestDTO)
			throws AccessDeniedException {
		UserInfo userInfo = getLoggedInSPInfo(regSource);
	    if (!UserType.Developer.name().equals(userInfo.role)) {
	        throw new AccessDeniedException("Only Developer users can access this API.");
	    }

	    String userId = requestDTO.getUserId();

	    // Map to store current max count for each prefix
	    Map<String, Long> prefixCounter = new HashMap<>();

	    List<SPPlumbingTasksManagemnt> resultList = new ArrayList<>();

	    for (SPPlumbingTasksManagemnt task : requestDTO.getTasks()) {
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
	public List<SPPlumbingTasksManagemnt> updateAdmin(RegSource regSource, List<SPPlumbingTasksManagemnt> taskList)
			throws AccessDeniedException {
		 UserInfo userInfo = getLoggedInSPInfo(regSource);
		    if (!UserType.Developer.name().equals(userInfo.role)) {
		        throw new AccessDeniedException("Only Developer users can access this API.");
		    }

		    List<SPPlumbingTasksManagemnt> updatedTasks = new ArrayList<>();

		    for (SPPlumbingTasksManagemnt task : taskList) {
		        String taskIdKey = task.getUserIdServiceCategoryTaskId();

		        if (taskIdKey == null || taskIdKey.isEmpty()) {
		            throw new ResourceNotFoundException("userId is required for update.");
		        }

		        // Fetch the existing entity
		        SPPlumbingTasksManagemnt existingTask = repository.findById(taskIdKey)
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

}
