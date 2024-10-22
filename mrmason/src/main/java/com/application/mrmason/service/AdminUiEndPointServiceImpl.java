package com.application.mrmason.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AdminUiEndPointDto;
import com.application.mrmason.entity.AdminDetails;
import com.application.mrmason.entity.AdminUiEndPoint;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminDetailsRepo;
import com.application.mrmason.repository.AdminUiEndPointRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class AdminUiEndPointServiceImpl implements AdminUiEndPointService {

	@Autowired
	private AdminUiEndPointRepo adminUiEndPointRepo;
	
	@Autowired
	private AdminDetailsRepo adminLoginRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public AdminUiEndPointDto createAdminAcademicQualification(AdminUiEndPointDto adminUiEndPointDto,String adminName) {
	
		Optional<AdminDetails> adminLogin = adminLoginRepo.findByAdminName(adminName);
		if(adminLogin.isPresent()) {
			AdminDetails adminDb = adminLogin.get();
	        Optional<AdminUiEndPoint> existingRecord = adminUiEndPointRepo.findByUpdatedBy(adminUiEndPointDto.getUpdatedBy()); // Replace with actual unique field check
	        if (existingRecord.isPresent()) {
	            // Return the existing record's DTO if it is found
	            throw new ResourceNotFoundException("Record already exists for the given unique field");
	        }
			AdminUiEndPoint adminUiEndPoint = dtoToAdminUiEndPoint(adminUiEndPointDto);
			adminUiEndPoint.setUpdatedBy(adminDb.getAdminName());
			AdminUiEndPoint savedAdminUiEndPoint= adminUiEndPointRepo
					.save(adminUiEndPoint);
			return adminUiEndPointToDto(savedAdminUiEndPoint);
		}else {
			throw new ResourceNotFoundException("admin name is not available "+adminName);
		}
	}

	private AdminUiEndPointDto adminUiEndPointToDto(AdminUiEndPoint adminUiEndPoint) {
		AdminUiEndPointDto adminUiEndPointDto = modelMapper.map(adminUiEndPoint,
				AdminUiEndPointDto.class);
		return adminUiEndPointDto;
	}

	private AdminUiEndPoint dtoToAdminUiEndPoint(AdminUiEndPointDto adminUiEndPointDto) {
		AdminUiEndPoint adminUiEndPoint = modelMapper.map(adminUiEndPointDto,
				AdminUiEndPoint.class);
		return adminUiEndPoint;
	}

	@Override
	public List<AdminUiEndPoint> getAdminUiEndPointDto(String systemId,String ipUrlToUi,String updatedBy) {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<AdminUiEndPoint> query = cb.createQuery(AdminUiEndPoint.class);
		Root<AdminUiEndPoint> root = query.from(AdminUiEndPoint.class);
		List<Predicate> predicates = new ArrayList<>();
		if (systemId != null) {
			predicates.add(cb.equal(root.get("systemId"), systemId));
		}
		if (ipUrlToUi != null) {
			predicates.add(cb.equal(root.get("ipUrlToUi"), ipUrlToUi));
		}
		if (updatedBy != null) {
			predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
		}
		query.where(predicates.toArray(new Predicate[0]));

		return entityManager.createQuery(query).getResultList();
	}

	@Override
	public AdminUiEndPointDto updateAdminUiEndPoint(AdminUiEndPointDto adminUiEndPointDto, String systemId) {
		AdminUiEndPoint adminUiEndPoint=adminUiEndPointRepo.findById(systemId).orElseThrow(()->new ResourceNotFoundException(systemId));
		adminUiEndPoint.setIpUrlToUi(adminUiEndPointDto.getIpUrlToUi());
		adminUiEndPoint.setUpdatedBy(adminUiEndPointDto.getUpdatedBy());
		AdminUiEndPoint updatedAdminUiEndPoint=adminUiEndPointRepo.save(adminUiEndPoint);
		AdminUiEndPointDto adminAdminUiEndPointDto1=this.adminUiEndPointToDto(updatedAdminUiEndPoint);
		return adminAdminUiEndPointDto1;
	}

}
