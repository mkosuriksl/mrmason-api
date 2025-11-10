package com.application.mrmason.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.entity.WorkBreakdownElements;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.WorkBreakdownElementsService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class WorkBreakdownElementsServiceImpl implements WorkBreakdownElementsService {
	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional
	public WorkBreakdownElements addWorkBreakdownElements(WorkBreakdownElements workBreakdownElements,RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		// Try to find existing entity by PK
		WorkBreakdownElements existing = entityManager.find(WorkBreakdownElements.class,
				workBreakdownElements.getWoOrderNo());
		Date now = new Date();
		if (existing != null) {
			existing.setTaskId(workBreakdownElements.getTaskId());
			existing.setSubTaskId(workBreakdownElements.getSubTaskId());
			existing.setUpdatedBy(userInfo.userId);
			existing.setUpdatedDate(now);
			existing.setActualStartDate(workBreakdownElements.getActualStartDate());
			existing.setActualEndDate(workBreakdownElements.getActualEndDate());
			existing.setTentativeStartdate(workBreakdownElements.getTentativeStartdate());
			existing.setActualEndDate(workBreakdownElements.getActualEndDate());

			WorkBreakdownElements merged = entityManager.merge(existing);
			return merged;
		} else {
			workBreakdownElements.setWoOrderNo(workBreakdownElements.getWoOrderNo());
			workBreakdownElements.setUpdatedBy(userInfo.userId);
			workBreakdownElements.setUpdatedDate(now);

			entityManager.persist(workBreakdownElements);
			return workBreakdownElements;
		}
	}

	private static class UserInfo {

		String userId;

		UserInfo(String userId) {
			this.userId = userId;
		}
	}

	private UserInfo getLoggedInUserInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());

		if (roleNames.equals("Developer")) {
			throw new ResourceNotFoundException("Restricted role: " + roleNames);
		}

		UserType userType = UserType.valueOf(roleNames.get(0));
		String userId;

		if (userType == UserType.Adm) {
			AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
					.orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
			userId = admin.getAdminId(); // or any other logic you want
		} else {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}

		return new UserInfo(userId);
	}

	@Override
	@Transactional
	public WorkBreakdownElements updateWorkBreakdownElements(WorkBreakdownElements workBreakdownElements,RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		String pk = workBreakdownElements.getWoOrderNo();
		if (pk == null || pk.isEmpty()) {
			throw new IllegalArgumentException("work order number is required for update");
		}
		WorkBreakdownElements existing = entityManager.find(WorkBreakdownElements.class, pk);
		if (existing == null) {
			throw new ResourceNotFoundException("Machine asset not found for ID: " + pk);
		}
		if (workBreakdownElements.getTaskId() != null) {
			existing.setTaskId(workBreakdownElements.getTaskId());
		}
		if (workBreakdownElements.getSubTaskId() != null) {
			existing.setSubTaskId(workBreakdownElements.getSubTaskId());
		}
		if (workBreakdownElements.getActualEndDate() != null) {
			existing.setActualEndDate(workBreakdownElements.getActualEndDate());
		}
		if (workBreakdownElements.getActualStartDate() != null) {
			existing.setActualStartDate(workBreakdownElements.getActualStartDate());
		}
		if (workBreakdownElements.getTentativeStartdate() != null) {
			existing.setTentativeStartdate(workBreakdownElements.getTentativeStartdate());
		}
		if (workBreakdownElements.getTentaiveEnddate() != null) {
			existing.setTentaiveEnddate(workBreakdownElements.getTentaiveEnddate());
		}
		existing.setUpdatedBy(userInfo.userId);
		existing.setUpdatedDate(new Date());
		return entityManager.merge(existing);
	}

	@Override
	public Page<WorkBreakdownElements> get(String woOrderNo, String taskId, String subTaskId,
	        String actualStartDateStr, String actualEndDateStr,
	        String tentativeStartDateStr, String tentaiveEnddateStr, Pageable pageable) {

	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<WorkBreakdownElements> query = cb.createQuery(WorkBreakdownElements.class);
	    Root<WorkBreakdownElements> root = query.from(WorkBreakdownElements.class);

	    List<Predicate> predicates = new ArrayList<>();

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date actualStartDate = null, actualEndDate = null, tentativeStartDate = null, tentaiveEndDate = null;

	    try {
	        if (actualStartDateStr != null && !actualStartDateStr.isEmpty()) {
	            actualStartDate = sdf.parse(actualStartDateStr);
	        }
	        if (actualEndDateStr != null && !actualEndDateStr.isEmpty()) {
	            actualEndDate = sdf.parse(actualEndDateStr);
	        }
	        if (tentativeStartDateStr != null && !tentativeStartDateStr.isEmpty()) {
	            tentativeStartDate = sdf.parse(tentativeStartDateStr);
	        }
	        if (tentaiveEnddateStr != null && !tentaiveEnddateStr.isEmpty()) {
	            tentaiveEndDate = sdf.parse(tentaiveEnddateStr);
	        }
	    } catch (ParseException e) {
	        throw new RuntimeException("Invalid date format. Expected yyyy-MM-dd", e);
	    }

	    if (woOrderNo != null && !woOrderNo.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("woOrderNo"), woOrderNo));
	    }
	    if (taskId != null && !taskId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("taskId"), taskId));
	    }
	    if (subTaskId != null && !subTaskId.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("subTaskId"), subTaskId));
	    }
	    if (actualStartDate != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("actualStartDate"), actualStartDate));
	    }
	    if (actualEndDate != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("actualEndDate"), actualEndDate));
	    }
	    if (tentativeStartDate != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("tentativeStartdate"), tentativeStartDate));
	    }
	    if (tentaiveEndDate != null) {
	        predicates.add(cb.greaterThanOrEqualTo(root.get("tentaiveEnddate"), tentaiveEndDate));
	    }

	    query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

	    TypedQuery<WorkBreakdownElements> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // Count query
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<WorkBreakdownElements> countRoot = countQuery.from(WorkBreakdownElements.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (woOrderNo != null && !woOrderNo.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("woOrderNo"), woOrderNo));
	    }
	    if (taskId != null && !taskId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("taskId"), taskId));
	    }
	    if (subTaskId != null && !subTaskId.trim().isEmpty()) {
	        countPredicates.add(cb.equal(countRoot.get("subTaskId"), subTaskId));
	    }
	    if (actualStartDate != null) {
	        countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("actualStartDate"), actualStartDate));
	    }
	    if (actualEndDate != null) {
	        countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("actualEndDate"), actualEndDate));
	    }
	    if (tentativeStartDate != null) {
	        countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("tentativeStartdate"), tentativeStartDate));
	    }
	    if (tentaiveEndDate != null) {
	        countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("tentaiveEnddate"), tentaiveEndDate));
	    }

	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();

	    return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}


}
