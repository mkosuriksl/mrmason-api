package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import com.application.mrmason.entity.WorkProgressDetails;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.repository.WorkProgressDetailsRepo;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.WorkProgressDetailsService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class WorkProgressDetailsServiceImpl implements WorkProgressDetailsService {
    @Autowired
    public AdminDetailsRepo adminRepo;

    @Autowired
    UserDAO userDAO;

    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private WorkProgressDetailsRepo detailsRepo;

    @Override
    @Transactional
    public WorkProgressDetails addWorkProgressDetails(WorkProgressDetails entity, RegSource regSource) {
        UserInfo userInfo = getLoggedInUserInfo(regSource);

        // Use Optional to check if record exists
        Optional<WorkProgressDetails> existingOpt = Optional.empty();

        if (entity.getOrderNoDate() != null) {
            existingOpt = detailsRepo.findById(entity.getOrderNoDate());
        }

        Date now = new Date();

        if (existingOpt.isPresent()) {
            // ✅ Update existing record
            WorkProgressDetails existing = existingOpt.get();
            existing.setTaskId(entity.getTaskId());
            existing.setSubTaskId(entity.getSubTaskId());
            existing.setWorkDescription(entity.getWorkDescription());
            existing.setDateOfWork(entity.getDateOfWork());
            existing.setNoOfResource(entity.getNoOfResource());
            existing.setUpdatedBy(userInfo.userId);
            existing.setUpdatedDate(now);

            return detailsRepo.save(existing);
        } else {
            // ✅ Create new record
            entity.setUpdatedBy(userInfo.userId);
            entity.setUpdatedDate(now);
            return detailsRepo.save(entity);
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
    public WorkProgressDetails updateWorkProgressDetails(WorkProgressDetails entity,RegSource regSource) {
        UserInfo userInfo = getLoggedInUserInfo(regSource);
        String pk = entity.getOrderNoDate();
        if (pk == null || pk.isEmpty()) {
            throw new IllegalArgumentException("work order number is required for update");
        }
        WorkProgressDetails existing = entityManager.find(WorkProgressDetails.class, pk);
        if (existing == null) {
            throw new ResourceNotFoundException("Work progress element not found for ID: " + pk);
        }
        if (entity.getTaskId() != null) {
            existing.setTaskId(entity.getTaskId());
        }
        if (entity.getSubTaskId() != null) {
            existing.setSubTaskId(entity.getSubTaskId());
        }
        if (entity.getDateOfWork() != null) {
            existing.setDateOfWork(entity.getDateOfWork());
        }
        if (entity.getNoOfResource() != null) {
            existing.setNoOfResource(entity.getNoOfResource());
        }
        if (entity.getWorkDescription() != null) {
            existing.setWorkDescription(entity.getWorkDescription());
        }
     
        existing.setUpdatedBy(userInfo.userId);
        existing.setUpdatedDate(new Date());
        return entityManager.merge(existing);
    }

    @Override
    public Page<WorkProgressDetails> get(String orderNoDate, String orderNo, String workDescription,
            String taskId, String subTaskId, Pageable pageable) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<WorkProgressDetails> query = cb.createQuery(WorkProgressDetails.class);
        Root<WorkProgressDetails> root = query.from(WorkProgressDetails.class);

        List<Predicate> predicates = new ArrayList<>();


        if (orderNoDate != null && !orderNoDate.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("orderNoDate"), orderNoDate));
        }
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("orderNo"), orderNo));
        }
        if (workDescription != null && !workDescription.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("workDescription"), workDescription));
        }
        if (taskId != null && !taskId.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("taskId"), taskId));
        }
        if (subTaskId != null && !subTaskId.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("subTaskId"), subTaskId));
        }
        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<WorkProgressDetails> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<WorkProgressDetails> countRoot = countQuery.from(WorkProgressDetails.class);
        List<Predicate> countPredicates = new ArrayList<>();

        if (orderNoDate != null && !orderNoDate.trim().isEmpty()) {
        	countPredicates.add(cb.equal(countRoot.get("orderNoDate"), orderNoDate));
        }
        if (orderNo != null && !orderNo.trim().isEmpty()) {
        	countPredicates.add(cb.equal(countRoot.get("orderNo"), orderNo));
        }
        if (workDescription != null && !workDescription.trim().isEmpty()) {
        	countPredicates.add(cb.equal(countRoot.get("workDescription"), workDescription));
        }
        if (taskId != null && !taskId.trim().isEmpty()) {
        	countPredicates.add(cb.equal(countRoot.get("taskId"), taskId));
        }
        if (subTaskId != null && !subTaskId.trim().isEmpty()) {
        	countPredicates.add(cb.equal(countRoot.get("subTaskId"), subTaskId));
        }

        countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }

}
