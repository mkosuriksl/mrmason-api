package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
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

import com.application.mrmason.entity.GstInServiceUser;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.GstInServiceUserRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.GstInServiceUserService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class GstInServiceUserServiceImpl implements GstInServiceUserService {

	@Autowired
	private GstInServiceUserRepository gstRepo;

	@Autowired
	private UserDAO userDAO;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<GstInServiceUser> saveGst(List<GstInServiceUser> users, RegSource regSource) {
		UserInfo userInfo = getLoggedInSPInfo(regSource);

		Date now = new Date();

		users.forEach(u -> {
			u.setUpdatedDate(now);
			u.setUpdatedBy(userInfo.userId);
		});

		return gstRepo.saveAll(users);
	}

	@Override
	public List<GstInServiceUser> updateGst(List<GstInServiceUser> users, RegSource regSource) {
		UserInfo userInfo = getLoggedInSPInfo(regSource);

		Date now = new Date();

		users.forEach(u -> {
			u.setUpdatedDate(now);
			u.setUpdatedBy(userInfo.userId);
		});
		return gstRepo.saveAll(users);
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
	
	@Override
	public Page<GstInServiceUser> getGst(String bodSeqNo, String gst, String userId, RegSource regSource, Pageable pageable) throws AccessDeniedException {
		UserInfo userInfo = getLoggedInSPInfo(regSource);

		// ALLOW only Admin or Developer, block others
		 if (!UserType.Developer.name().equals(userInfo.role)) {
		        throw new AccessDeniedException("Only Developer users can access this API.");
		    }

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<GstInServiceUser> query = cb.createQuery(GstInServiceUser.class);
		Root<GstInServiceUser> root = query.from(GstInServiceUser.class);
		List<Predicate> predicates = new ArrayList<>();

		if (bodSeqNo != null && !bodSeqNo.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("bodSeqNo"), bodSeqNo));
		}
		if (gst != null && !gst.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("gst"), gst));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			predicates.add(cb.equal(root.get("updatedBy"), userId));
		}

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<GstInServiceUser> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());

		// Count query
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<GstInServiceUser> countRoot = countQuery.from(GstInServiceUser.class);
		List<Predicate> countPredicates = new ArrayList<>();

		if (bodSeqNo != null && !bodSeqNo.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("bodSeqNo"), bodSeqNo));
		}
		if (gst != null && !gst.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("gst"), gst));
		}
		if (userId != null && !userId.trim().isEmpty()) {
			countPredicates.add(cb.equal(countRoot.get("updatedBy"), userId));
		}


		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long total = entityManager.createQuery(countQuery).getSingleResult();

		return new PageImpl<>(typedQuery.getResultList(), pageable, total);
	}

}
