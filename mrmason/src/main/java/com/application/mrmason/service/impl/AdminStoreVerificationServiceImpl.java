package com.application.mrmason.service.impl;

import com.application.mrmason.dto.AdminStoreVerificationRequestDTO;
import com.application.mrmason.dto.AdminStoreVerificationResponse;
import com.application.mrmason.dto.AdminStoreVerificationResponseDTO;
import com.application.mrmason.entity.AdminMembershipPlanEntity;
import com.application.mrmason.entity.AdminStoreVerificationEntity;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.AdminMembershipPlanRepository;
import com.application.mrmason.repository.AdminStoreVerificationRepository;
import com.application.mrmason.repository.ServicePersonStoreDetailsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.AdminStoreVerificationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminStoreVerificationServiceImpl implements AdminStoreVerificationService {

    @Autowired
    private AdminStoreVerificationRepository repository;

    @Autowired
    private ServicePersonStoreDetailsRepo spStoreDetailsRepository;

    @Autowired
    private AdminMembershipPlanRepository adminmemPlanRepository;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EmailServiceImpl emailService;
    
	@PersistenceContext
	private EntityManager entityManager;

    @Override
    public AdminStoreVerificationResponseDTO verifyStore(AdminStoreVerificationRequestDTO requestDTO) {

        Optional<ServicePersonStoreDetailsEntity> storeDetails = spStoreDetailsRepository
                .findByBodSeqNoStoreId(requestDTO.getBodSeqNoStoreId());

        if (storeDetails.isEmpty()) {
            throw new EntityNotFoundException("Store not found");
        }

        User user = userDAO.findByBodSeqNo(requestDTO.getBodSeqNo());
        if (user == null) {
            throw new EntityNotFoundException("User not found for the given Service person");
        }

        AdminMembershipPlanEntity planEntity = null;

        if (requestDTO.getDefaultPlan() != null && !requestDTO.getDefaultPlan().isEmpty()) {
            Optional<AdminMembershipPlanEntity> planEntities = adminmemPlanRepository
                    .findByDefaultPlan(requestDTO.getDefaultPlan());

            if (planEntities.isPresent()) {
                planEntity = planEntities.get();
                log.info("Plan found for store {}: {}", requestDTO.getStoreId(), planEntity.getPlanName());
            } else {
                log.warn("No membership plan found for the given default plan");
            }
        } else {
            log.info("Default plan not provided; defaultPlanData will be null");
        }

        Optional<AdminStoreVerificationEntity> existingVerification = repository
                .findByBodSeqNoStoreId(requestDTO.getBodSeqNoStoreId());

        AdminStoreVerificationEntity verification;
        if (existingVerification.isPresent()) {

            verification = existingVerification.get();
            log.info("Updating existing store verification for bodSeqNoStoreId: {}", requestDTO.getBodSeqNoStoreId());
        } else {

            verification = new AdminStoreVerificationEntity();
            verification.setBodSeqNoStoreId(requestDTO.getBodSeqNoStoreId());
            log.info("Creating new store verification for bodSeqNoStoreId: {}", requestDTO.getBodSeqNoStoreId());
        }

        verification.setStoreId(requestDTO.getStoreId());
        verification.setBodSeqNo(requestDTO.getBodSeqNo());
        verification.setDefaultPlan(requestDTO.getDefaultPlan());
        verification.setVerificationStatus(requestDTO.getVerificationStatus());
        verification.setVerificationComment(requestDTO.getVerificationComment());
        verification.setUpdatedBy(requestDTO.getUpdatedBy());

        AdminStoreVerificationEntity savedVerification = repository.save(verification);
        log.info("Store verification saved with bodSeqNoStoreId: {}", savedVerification.getBodSeqNoStoreId());

        String email = user.getEmail();
        if (email != null && !email.isEmpty()) {
            String subject = "Store Verification Status Updated";
            String body = String.format(
                    "<html><body>" +
                            "<h3>Hello %s,</h3>" +
                            "<p>Your store verification status has been updated to: <b>%s</b>.</p>" +
                            "<b>Description:</b> %s" +
                            "<p>Visit <a href='https://www.mekanik.in'>www.mekanik.in</a> for more details.</p>" +
                            "<p>Best regards,<br/>The Mekanik Team</p>" +
                            "</body></html>",
                    user.getName(), requestDTO.getVerificationStatus(), requestDTO.getVerificationComment());

            try {
                emailService.sendEmail(email, subject, body);
                log.info("Verification status email sent to: {}", email);
            } catch (Exception e) {
                log.error("Failed to send email notification: {}", e.getMessage(), e);
            }
        }

        AdminStoreVerificationResponseDTO responseDTO = new AdminStoreVerificationResponseDTO();
        BeanUtils.copyProperties(savedVerification, responseDTO);

        responseDTO.setDefaultPlanData(planEntity);

        return responseDTO;
    }

//    public AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>> getVerificationsByParams(
//            String storeId, String bodSeqNo, String bodSeqNoStoreId, String verificationStatus, String updatedBy) {
//
//        log.info(
//                "Fetching verifications with parameters - storeId: {}, bodSeqNo: {}, bodSeqNoStoreId:{}, verificationStatus: {}, updatedBy: {}",
//                storeId, bodSeqNo, bodSeqNoStoreId, verificationStatus, updatedBy);
//
//        try {
//            List<AdminStoreVerificationEntity> entities = repository.findByOptionalParams(storeId, bodSeqNo,
//                    bodSeqNoStoreId, verificationStatus, updatedBy);
//            log.debug("Found {} verification records", entities.size());
//
//            List<AdminStoreVerificationResponseDTO> responseDTOs = entities.stream().map(entity -> {
//                AdminStoreVerificationResponseDTO dto = new AdminStoreVerificationResponseDTO();
//                BeanUtils.copyProperties(entity, dto);
//                return dto;
//            }).collect(Collectors.toList());
//
//            return new AdminStoreVerificationResponse<>("Status of Store verifications fetched successfully", "SUCCESS",
//                    responseDTOs);
//
//        } catch (Exception e) {
//            log.error("Error fetching verifications: {}", e.getMessage(), e);
//            return new AdminStoreVerificationResponse<>("Error fetching store verifications", "FAILURE", null);
//        }
//    }
    
    public AdminStoreVerificationResponse<List<AdminStoreVerificationResponseDTO>> getVerificationsByParams(
            String storeId, String bodSeqNo, String bodSeqNoStoreId, String verificationStatus,
            String updatedBy, int page, int size) {

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<AdminStoreVerificationEntity> query = cb.createQuery(AdminStoreVerificationEntity.class);
            Root<AdminStoreVerificationEntity> root = query.from(AdminStoreVerificationEntity.class);

            List<Predicate> predicates = new ArrayList<>();

            if (storeId != null) predicates.add(cb.equal(root.get("storeId"), storeId));
            if (bodSeqNo != null) predicates.add(cb.equal(root.get("bodSeqNo"), bodSeqNo));
            if (bodSeqNoStoreId != null) predicates.add(cb.equal(root.get("bodSeqNoStoreId"), bodSeqNoStoreId));
            if (verificationStatus != null) predicates.add(cb.equal(root.get("verificationStatus"), verificationStatus));
            if (updatedBy != null) predicates.add(cb.equal(root.get("updatedBy"), updatedBy));

            query.where(cb.and(predicates.toArray(new Predicate[0])));
            query.orderBy(cb.desc(root.get("updatedDate")));

            // Apply pagination
            TypedQuery<AdminStoreVerificationEntity> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult(page * size);
            typedQuery.setMaxResults(size);

            List<AdminStoreVerificationEntity> resultList = typedQuery.getResultList();

            // Total count query
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<AdminStoreVerificationEntity> countRoot = countQuery.from(AdminStoreVerificationEntity.class);
            countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            List<AdminStoreVerificationResponseDTO> dtos = resultList.stream().map(entity -> {
                AdminStoreVerificationResponseDTO dto = new AdminStoreVerificationResponseDTO();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }).collect(Collectors.toList());

            return new AdminStoreVerificationResponse<>("Status of Store verifications fetched successfully", "SUCCESS", dtos,
                    page, size, totalElements, (int) Math.ceil((double) totalElements / size));

        } catch (Exception e) {
            log.error("Error fetching verifications: {}", e.getMessage(), e);
            return new AdminStoreVerificationResponse<>("Error fetching store verifications", "FAILURE", null);
        }
    }


}
