package com.application.mrmason.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminSpVerificationRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class AdminSPVerificationService{

	@Autowired
	private AdminSpVerificationRepository repository;

	@Autowired
	public AdminDetailsRepo adminRepo;

	@Autowired
	UserDAO userDAO;

	@PersistenceContext
	private EntityManager entityManager;



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

	public List<AdminSpVerification> updateAdmin(List<AdminSpVerification> taskList) {
		AdminInfo userInfo = getLoggedInAdminInfo();

		for (AdminSpVerification task : taskList) {
			if (task.getBodSeqNo() == null || task.getBodSeqNo().isEmpty()) {
				throw new ResourceNotFoundException("admin BodSeqNo is required for update.");
			}

			boolean exists = repository.existsById(task.getBodSeqNo());
			if (!exists) {
				throw new ResourceNotFoundException("admin BodSeqNo not found: " + task.getBodSeqNo());
			}
			task.setUpdatedDate(new Date());
			task.setUpdateBy(userInfo.adminId);
		}

		return repository.saveAll(taskList);
	}
}
