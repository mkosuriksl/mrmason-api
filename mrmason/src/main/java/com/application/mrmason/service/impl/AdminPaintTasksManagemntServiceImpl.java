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
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AdminPaintTaskRequestDTO;
import com.application.mrmason.dto.MeasureTaskDto;
import com.application.mrmason.dto.TaskResponseDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminPaintTasksManagemnt;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminPaintTasksManagemntRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.AdminPaintTasksManagemntService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminPaintTasksManagemntServiceImpl implements AdminPaintTasksManagemntService {

	@Autowired
	private AdminPaintTasksManagemntRepository repository;

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<AdminPaintTasksManagemnt> createAdmin(AdminPaintTaskRequestDTO requestDTO) {
		AdminInfo userInfo = getLoggedInAdminInfo();
		  Map<String, Long> prefixCounter = new HashMap<>();
		for (AdminPaintTasksManagemnt task : requestDTO.getTasks()) {
			String shortServiceCategory = task.getServiceCategory() != null && task.getServiceCategory().length() > 4
	                ? task.getServiceCategory().substring(0, 4)
	                : task.getServiceCategory();

	        String prefix = userInfo.adminId + "_" + shortServiceCategory + "_" + task.getTaskId() + "_";

	        // Get current count from DB (only once per prefix)
	        long currentCount = prefixCounter.computeIfAbsent(prefix, k -> repository.countByPrefix(k));

	        // Increment and generate new ID
	        currentCount++;
	        prefixCounter.put(prefix, currentCount); // Update counter for next one

	        String generatedId = prefix + String.format("%04d", currentCount);
	        task.setAdminTaskId(generatedId);
//			task.setAdminTaskId(userInfo.adminId+"_");
			task.setUserId(requestDTO.getUserId()); // Set common userId
			task.setUpdatedDate(new Date());
			task.setUpdatedBy(userInfo.userEmail);
		}

		return repository.saveAll(requestDTO.getTasks());
	}

	private static class AdminInfo {

		String userEmail;
		String adminId;
		AdminInfo(String userEmail,String adminId) {
			this.userEmail = userEmail;
			this.adminId=adminId;
		}
	}

	private AdminInfo getLoggedInAdminInfo() {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		// Allow only Adm role
		if (roleNames.isEmpty() || !roleNames.contains("Adm")) {
			throw new ResourceNotFoundException("Only Adm role is allowed. Found: " + roleNames);
		}

		// Get Admin details
		AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, UserType.Adm)
				.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));

		String userEmail = admin.getEmail(); 
		String adminId=admin.getAdminId();

		return new AdminInfo(userEmail,adminId);
	}

	@Override
	public List<AdminPaintTasksManagemnt> updateAdmin(List<AdminPaintTasksManagemnt> taskList) {
		AdminInfo userInfo = getLoggedInAdminInfo();

		for (AdminPaintTasksManagemnt task : taskList) {
			if (task.getAdminTaskId() == null || task.getAdminTaskId().isEmpty()) {
				throw new ResourceNotFoundException("adminTaskId is required for update.");
			}

			boolean exists = repository.existsById(task.getAdminTaskId());
			if (!exists) {
				throw new ResourceNotFoundException("Task not found for adminTaskId: " + task.getAdminTaskId());
			}

			task.setUserId(userInfo.userEmail);
			task.setUpdatedDate(new Date());
			task.setUpdatedBy(userInfo.userEmail);
		}

		return repository.saveAll(taskList);
	}

	@Override
	public Page<AdminPaintTasksManagemnt> getServiceRequestPaintQuotationService(String serviceCategory,
			String taskName, String taskId, String adminTaskId, RegSource regSource, Pageable pageable)
			throws AccessDeniedException {

		UserInfo userInfo = getLoggedInAdminSPInfo(regSource);

		// ALLOW only Admin or Developer, block others
		if (!userInfo.role.equals("Adm") && !userInfo.role.equals("Developer")) {
			throw new AccessDeniedException(
					"Access denied: only Admin or Developer roles are allowed to access this resource.");
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminPaintTasksManagemnt> query = cb.createQuery(AdminPaintTasksManagemnt.class);
		Root<AdminPaintTasksManagemnt> root = query.from(AdminPaintTasksManagemnt.class);
		List<Predicate> predicates = new ArrayList<>();

		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));
		}
		if (taskName != null && !taskName.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("taskName"), taskName));
		}
		if (taskId != null && !taskId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("taskId"), taskId));
		}
		if (adminTaskId != null && !adminTaskId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("adminTaskId"), adminTaskId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<AdminPaintTasksManagemnt> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<AdminPaintTasksManagemnt> countRoot = countQuery.from(AdminPaintTasksManagemnt.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (serviceCategory != null && !serviceCategory.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("serviceCategory"), serviceCategory));
		}
		if (taskName != null && !taskName.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("taskName"), taskName));
		}
		if (taskId != null && !taskId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("taskId"), taskId));
		}
		if (adminTaskId != null && !adminTaskId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("adminTaskId"), adminTaskId));
		}

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

	private static class UserInfo {
		String userId;
		String role;

		UserInfo(String userId, String role) {
			this.userId = userId;
			this.role = role;
		}
	}

	private UserInfo getLoggedInAdminSPInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		String userId;
		String role = roleNames.get(0); // Assuming only one role

		UserType userType = UserType.valueOf(role);

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getEmail();
		} else {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new UserInfo(userId, role);
	}

	@Override
	public List<TaskResponseDto> getTaskDetails(String serviceCategory, String taskId, String taskName) {
	    List<AdminPaintTasksManagemnt> records = repository.findByFilters(serviceCategory, taskId, taskName);

	    // Group records by task identity: serviceCategory + taskId + taskName
	    Map<String, List<AdminPaintTasksManagemnt>> grouped = records.stream()
	        .collect(Collectors.groupingBy(record -> 
	            record.getServiceCategory() + "|" + record.getTaskId() + "|" + record.getTaskName()
	        ));

	    List<TaskResponseDto> responseList = new ArrayList<>();

	    for (Map.Entry<String, List<AdminPaintTasksManagemnt>> entry : grouped.entrySet()) {
	        List<AdminPaintTasksManagemnt> groupRecords = entry.getValue();

	        // Get first record as a template for serviceCategory, taskId, taskName
	        AdminPaintTasksManagemnt first = groupRecords.get(0);

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
