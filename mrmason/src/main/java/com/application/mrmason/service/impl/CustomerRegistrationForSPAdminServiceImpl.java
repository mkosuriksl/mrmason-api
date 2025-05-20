package com.application.mrmason.service.impl;

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

import com.application.mrmason.dto.CustomerRegistrationRequestForSPAdmin;
import com.application.mrmason.dto.CustomerRegistrationRespForSPAdmin;
import com.application.mrmason.dto.CustomerRegistrationResponseForSPAdmin;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.SpWorkers;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.service.CustomerRegistrationForSPAdminService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class CustomerRegistrationForSPAdminServiceImpl implements CustomerRegistrationForSPAdminService {

	@Autowired
	private CustomerRegistrationRepo repo;

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public CustomerRegistrationResponseForSPAdmin registerCustomer(CustomerRegistrationRequestForSPAdmin dto,
			RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		repo.findByUserEmailOne(dto.getUserEmail()).ifPresent(existing -> {
			throw new IllegalArgumentException("Email already exists: " + dto.getUserEmail());
		});

		// ✅ Check for duplicate mobile
		repo.findByUserMobileOne(dto.getUserMobile()).ifPresent(existing -> {
			throw new IllegalArgumentException("Mobile number already exists: " + dto.getUserMobile());
		});
		CustomerRegistration customer = new CustomerRegistration();
		customer.setUserEmail(dto.getUserEmail());
		customer.setUserMobile(dto.getUserMobile());
		customer.setUserName(dto.getUserName());
		customer.setUserTown(dto.getUserTown());
		customer.setUserDistrict(dto.getUserDistrict());
		customer.setUserState(dto.getUserState());
		customer.setUserPincode(dto.getUserPincode());
		customer.setUserType(userInfo.userType);;
		customer.setUpdatedBy(userInfo.userId);
		customer.setUpdatedDate(new Date());

		repo.save(customer);

		CustomerRegistrationRespForSPAdmin responseDto = new CustomerRegistrationRespForSPAdmin();
		responseDto.setId(customer.getId());
		responseDto.setUserid(customer.getUserid()); // make sure getUserid() exists
		responseDto.setUserEmail(customer.getUserEmail());
		responseDto.setUserMobile(customer.getUserMobile());
		responseDto.setUserType(customer.getUserType().toString());
		responseDto.setUserTown(customer.getUserTown());
		responseDto.setUserDistrict(customer.getUserDistrict());
		responseDto.setUserState(customer.getUserState());
		responseDto.setUserPincode(customer.getUserPincode());
		responseDto.setRegDate(customer.getRegDate());
		responseDto.setUpdatedBy(customer.getUpdatedBy());
		responseDto.setUpdatedDate(customer.getUpdatedDate().toInstant().toString());
		responseDto.setUserName(dto.getUserName());

		return new CustomerRegistrationResponseForSPAdmin("Customer registered successfully", "success", responseDto);


	}
	private static class UserInfo {
	    String userId;
	    UserType userType;

	    UserInfo(String userId, UserType userType) {
	        this.userId = userId;
	        this.userType = userType;
	    }
	}
	private UserInfo getLoggedInUserInfo(RegSource regSource) {
	    String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
	    Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

	    List<String> roleNames = loggedInRole.stream()
	            .map(GrantedAuthority::getAuthority)
	            .map(role -> role.replace("ROLE_", ""))
	            .collect(Collectors.toList());

	    if (roleNames.contains("Developer")) {
	        throw new ResourceNotFoundException("Restricted role: " + roleNames);
	    }

	    UserType userType = UserType.valueOf(roleNames.get(0));
	    String userId;

	    if (userType == UserType.Adm) {
	        AdminDetails admin = adminRepo.findByEmailAndUserType(loggedInUserEmail, userType)
	                .orElseThrow(() -> new ResourceNotFoundException("Admin not found: " + loggedInUserEmail));
	        userId = admin.getEmail(); // or any other logic you want
	    } else {
	        User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
	                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
	        userId = user.getBodSeqNo();
	    }

	    return new UserInfo(userId, userType);
	}

	@Override
	public CustomerRegistrationResponseForSPAdmin updateCustomer(CustomerRegistrationRequestForSPAdmin dto,RegSource regSource) {
		UserInfo userInfo = getLoggedInUserInfo(regSource);
		CustomerRegistration customer = repo.findByUserids(dto.getUserId()).orElseThrow(
				() -> new ResourceNotFoundException("Customer not found with userId: " + dto.getUserId()));
		customer.setUserMobile(dto.getUserMobile());
		customer.setUserName(dto.getUserName());
		customer.setUserEmail(dto.getUserEmail());
		customer.setUserTown(dto.getUserTown());
		customer.setUserDistrict(dto.getUserDistrict());
		customer.setUserState(dto.getUserState());
		customer.setUserPincode(dto.getUserPincode());
		customer.setUpdatedBy(userInfo.userId);
		customer.setUserType(userInfo.userType);
		customer.setUpdatedDate(new Date());

		repo.save(customer);

		CustomerRegistrationRespForSPAdmin responseDto = new CustomerRegistrationRespForSPAdmin();
		responseDto.setId(customer.getId());
		responseDto.setUserid(customer.getUserid()); // make sure getUserid() exists
		responseDto.setUserEmail(customer.getUserEmail());
		responseDto.setUserMobile(customer.getUserMobile());
		responseDto.setUserType(customer.getUserType().toString());
		responseDto.setUserTown(customer.getUserTown());
		responseDto.setUserDistrict(customer.getUserDistrict());
		responseDto.setUserState(customer.getUserState());
		responseDto.setUserPincode(customer.getUserPincode());
		responseDto.setRegDate(customer.getRegDate());
		responseDto.setUpdatedBy(customer.getUpdatedBy());
		responseDto.setUpdatedDate(customer.getUpdatedDate().toInstant().toString());
		responseDto.setUserName(dto.getUserName());

		return new CustomerRegistrationResponseForSPAdmin("Customer Updated successfully", "success", responseDto);
	}
	
	@Override
	public Page<CustomerRegistrationRespForSPAdmin> getCustomerRegistration(String userid, String userEmail, String userMobile, String userTown, Pageable pageable) {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // === Selection query ===
	    CriteriaQuery<CustomerRegistrationRespForSPAdmin> query = cb.createQuery(CustomerRegistrationRespForSPAdmin.class);
	    Root<CustomerRegistration> root = query.from(CustomerRegistration.class);
	    List<Predicate> predicates = new ArrayList<>();

	    if (userid != null && !userid.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("userid"), userid));
	    }
	    if (userEmail != null && !userEmail.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("userEmail"), userEmail));
	    }
	    if (userMobile != null && !userMobile.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("userMobile"), userMobile));
	    }
	    if (userTown != null && !userTown.trim().isEmpty()) {
	        predicates.add(cb.equal(root.get("userTown"), userTown));
	    }
	    
	    query.select(cb.construct(CustomerRegistrationRespForSPAdmin.class,
	    	    root.get("id"),                          // Long
	    	    root.get("userid"),                      // String
	    	    root.get("userEmail"),                   // String
	    	    root.get("userMobile"),                  // String
	    	    root.get("userType").as(String.class),  // Convert enum to String
	    	    root.get("userTown"),                    // String
	    	    root.get("userDistrict"),                // String
	    	    root.get("userState"),                   // String
	    	    root.get("userPincode"),                 // String
	    	    root.get("regDate").as(String.class),   // Date to String
	    	    root.get("updatedBy"),                   // String
	    	    root.get("updatedDate").as(String.class), // Date to String
	    	    root.get("userName")                     // String
	    	))
	    	.where(cb.and(predicates.toArray(new Predicate[0])));

	    TypedQuery<CustomerRegistrationRespForSPAdmin> typedQuery = entityManager.createQuery(query);
	    typedQuery.setFirstResult((int) pageable.getOffset());
	    typedQuery.setMaxResults(pageable.getPageSize());

	    // === Count query ===
	    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
	    Root<CustomerRegistration> countRoot = countQuery.from(CustomerRegistration.class);
	    List<Predicate> countPredicates = new ArrayList<>();

	    if (userid != null && !userid.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("userid"), userid));
	    }
	    if (userEmail != null && !userEmail.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("userEmail"), userEmail));
	    }
	    if (userMobile != null && !userMobile.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("userMobile"), userMobile));
	    }
	    if (userTown != null && !userTown.trim().isEmpty()) {
	    	countPredicates.add(cb.equal(countRoot.get("userTown"), userTown));
	    }
	    countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
	    Long total = entityManager.createQuery(countQuery).getSingleResult();
	    
	    List<CustomerRegistrationRespForSPAdmin> resultList = typedQuery.getResultList();

	    return new PageImpl<>(resultList, pageable, total);
	}

}
