package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.ResponseSPMembRenewalDetailsDto;
import com.application.mrmason.dto.SPMembRenewDetailsRequestDTO;
import com.application.mrmason.dto.SPMembRenewDetailsResponseDTO;
import com.application.mrmason.entity.AdminMembershipPlanEntity;
import com.application.mrmason.entity.AdminStoreVerificationEntity;
import com.application.mrmason.entity.SPMembRenewDetailsEntity;
import com.application.mrmason.entity.SPMembRenewHeaderEntity;
import com.application.mrmason.repository.AdminMembershipPlanRepository;
import com.application.mrmason.repository.AdminStoreVerificationRepository;
import com.application.mrmason.repository.SPMembRenewDetailsRepository;
import com.application.mrmason.repository.SPMembRenewHeaderRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SPMembRenewDetailsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SPMembRenewDetailsServiceImpl implements SPMembRenewDetailsService {

    @Autowired
    private SPMembRenewDetailsRepository repository;

    @Autowired
    private AdminStoreVerificationRepository adminStoreVerificationRepository;

    @Autowired
    private AdminMembershipPlanRepository adminMembershipPlanRepository;

    @Autowired
    private SPMembRenewHeaderRepo repo;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public ResponseSPMembRenewalDetailsDto addMembershipRenewalDetails(
            List<SPMembRenewDetailsRequestDTO> requestDTOList) {
        List<SPMembRenewDetailsResponseDTO> responseList = new ArrayList<>();
        ResponseSPMembRenewalDetailsDto response = new ResponseSPMembRenewalDetailsDto();

        try {

            String generatedOrderId = generateOrderId();
            int totalOrderAmount = 0;

            for (int i = 0; i < requestDTOList.size(); i++) {
                SPMembRenewDetailsRequestDTO requestDTO = requestDTOList.get(i);
                try {

                    String lineItemId = generateLineId(generatedOrderId, i + 1);

                    Optional<AdminStoreVerificationEntity> storeVerification = adminStoreVerificationRepository
                            .findByStoreIdAndBodSeqNo(requestDTO.getStoreId(), requestDTO.getOrderPlacedBy());

                    if (storeVerification.isEmpty()) {
                        response.setMessage("Admin should verify store.");
                        responseList.add(createErrorResponse(requestDTO, "N/A", "Failed"));
                        continue;
                    }

                    AdminMembershipPlanEntity planEntity = adminMembershipPlanRepository
                            .findById(requestDTO.getPlanId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Plan not found for planId: " + requestDTO.getPlanId()));

                    SPMembRenewDetailsEntity entity = mapToEntity(requestDTO);
                    entity.setMembershipOrderIdLineItem(lineItemId);
                    entity.setMembershipOrderId(generatedOrderId);
                    entity.setOrderAmount(planEntity.getAmount());

                    totalOrderAmount += planEntity.getAmount();

                    SPMembRenewDetailsEntity savedEntity = repository.save(entity);
                    responseList.add(mapToResponseDTO(savedEntity));

                } catch (Exception e) {
                    log.error("Error processing store for storeId: {}", requestDTO.getStoreId(), e);
                    responseList.add(createErrorResponse(requestDTO, "N/A", "Failed"));
                }
            }

            SPMembRenewHeaderEntity headerEntity = new SPMembRenewHeaderEntity();
            headerEntity.setMembershipOrderId(generatedOrderId);
            headerEntity.setOrderAmount(totalOrderAmount);
            headerEntity.setOrderDate(LocalDateTime.now());
            headerEntity.setOrderPlacedBy(requestDTOList.get(0).getOrderPlacedBy());
            headerEntity.setStatus("New");

            SPMembRenewHeaderEntity updatedHeader = repo.save(headerEntity);

            sendMembershipUpdateEmail(updatedHeader);

            response.setStatus(!responseList.isEmpty());
            response.setMessage("Membership renewal details processed successfully.");
            response.setRenewalDetailsList(responseList);

        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            response.setStatus(false);
            response.setMessage("Error occurred while processing membership renewal details.");
        }

        return response;
    }

    private void sendMembershipUpdateEmail(SPMembRenewHeaderEntity headerEntity) {
        String email = userDAO.findByBodSeqNo(headerEntity.getOrderPlacedBy()).getEmail();
        if (email != null && !email.isEmpty()) {
            String subject = "Membership Order Updated";
            String body = String.format(
                    "<html><body>" +
                            "<h3>Hello,</h3>" +
                            "<p>Your membership order with ID <b>%s</b> has been created.</p>" +
                            "<b>Order Status:</b> %s<br>" +
                            "<b>Total Amount:</b> %d<br>" +
                            "<p>Visit <a href='https://www.mekanik.in'>www.mekanik.in</a> for more details.</p>" +
                            "<p>Best regards,<br/>The Mekanik Team</p>" +
                            "</body></html>",
                    headerEntity.getMembershipOrderId(), headerEntity.getStatus(), headerEntity.getOrderAmount());
            try {
                emailService.sendEmail(email, subject, body);
                log.info("Membership order update email sent to: {}", email);
            } catch (Exception e) {
                log.error("Failed to send email notification: {}", e.getMessage(), e);
            }
        }
    }

    private SPMembRenewDetailsResponseDTO createErrorResponse(SPMembRenewDetailsRequestDTO requestDTO,
            String lineItemId,
            String status) {
        SPMembRenewDetailsResponseDTO errorResponse = new SPMembRenewDetailsResponseDTO();
        errorResponse.setMembershipOrderIdLineItem(lineItemId);
        errorResponse.setStoreId(requestDTO.getStoreId());
        errorResponse.setStatus(status);
        return errorResponse;
    }

    @Override
    public SPMembRenewDetailsResponseDTO updateMembershipRenewalDetails(String membershipOrderIdLineItem,
            SPMembRenewDetailsRequestDTO requestDTO) {
        SPMembRenewDetailsEntity entity = repository.findById(membershipOrderIdLineItem)
                .orElseThrow(() -> new RuntimeException("Membership renewal not found"));

        entity.setOrderAmount(requestDTO.getOrderAmount());
        entity.setOrderDate(requestDTO.getOrderDate());
        entity.setOrderPlacedBy(requestDTO.getOrderPlacedBy());
        entity.setPlanId(requestDTO.getPlanId());
        entity.setStatus(requestDTO.getStatus());
        entity.setStoreId(requestDTO.getStoreId());
        SPMembRenewDetailsEntity updatedEntity = repository.save(entity);
        return mapToResponseDTO(updatedEntity);
    }

    @Override
    public List<SPMembRenewDetailsResponseDTO> getAllMembershipRenewalDetails(
            String membershipOrderIdLineItem,
            String membershipOrderId,
            String storeId,
            String planId,
            Integer orderAmount,
            LocalDateTime orderDate,
            String orderPlacedBy,
            String status) {

        log.info(
                "Fetching membership renewal details with filters: membershipOrderIdLineItem={}, storeId={}, planId={}, orderAmount={}, orderDate={}, orderPlacedBy={}, status={}",
                membershipOrderIdLineItem, membershipOrderId, storeId, planId, orderAmount, orderDate, orderPlacedBy,
                status);

        try {

            List<SPMembRenewDetailsEntity> renewals = repository.findMembershipRenewalDetails(
                    membershipOrderIdLineItem, membershipOrderId, orderAmount, orderDate, orderPlacedBy, planId, status,
                    storeId);

            if (renewals != null && !renewals.isEmpty()) {
                log.info("Found {} membership renewal details.", renewals.size());
            } else {
                log.warn("No membership renewal details found for the given filters.");
            }

            return renewals.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error occurred while fetching membership renewal details: {}", e.getMessage());
            throw new RuntimeException("Error occurred while fetching membership renewal details.", e);
        }
    }

    @Override
    public void deleteMembershipRenewalDetails(String membershipOrderIdLineItem) {
        if (repository.existsById(membershipOrderIdLineItem)) {
            repository.deleteById(membershipOrderIdLineItem);
            log.info("Deleted membership renewal detail with ID: {}", membershipOrderIdLineItem);
        } else {
            throw new RuntimeException("Membership renewal detail not found with ID: " + membershipOrderIdLineItem);
        }
    }

    private String generateOrderId() {
        LocalDateTime now = LocalDateTime.now();

        String year = String.format("%04d", now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        String hour = String.format("%02d", now.getHour());
        String minute = String.format("%02d", now.getMinute());
        String second = String.format("%02d", now.getSecond());

        return "MEM" + year + month + day + hour + minute + second;
    }

    private String generateLineId(String generatedOrderId, int lineCounter) {
        return String.format("%s_%04d", generatedOrderId, lineCounter);
    }

    private SPMembRenewDetailsResponseDTO mapToResponseDTO(SPMembRenewDetailsEntity entity) {

        SPMembRenewDetailsResponseDTO dto = new SPMembRenewDetailsResponseDTO();
        dto.setMembershipOrderIdLineItem(entity.getMembershipOrderIdLineItem());
        dto.setMembershipOrderId(entity.getMembershipOrderId());
        dto.setOrderAmount(entity.getOrderAmount());
        dto.setOrderDate(entity.getOrderDate());
        dto.setOrderPlacedBy(entity.getOrderPlacedBy());
        dto.setPlanId(entity.getPlanId());
        dto.setStatus(entity.getStatus());
        dto.setStoreId(entity.getStoreId());
        return dto;
    }

    private SPMembRenewDetailsEntity mapToEntity(SPMembRenewDetailsRequestDTO dto) {
        SPMembRenewDetailsEntity entity = new SPMembRenewDetailsEntity();

        entity.setMembershipOrderIdLineItem(dto.getMembershipOrderIdLineItem());
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDateTime.now());
        entity.setOrderPlacedBy(dto.getOrderPlacedBy());
        entity.setPlanId(dto.getPlanId());
        entity.setStatus(dto.getStatus());
        entity.setStoreId(dto.getStoreId());

        return entity;
    }

}
