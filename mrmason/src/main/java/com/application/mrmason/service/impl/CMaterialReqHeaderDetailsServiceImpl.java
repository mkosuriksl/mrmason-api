package com.application.mrmason.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CMaterialReqHeaderDetailsDTO;
import com.application.mrmason.dto.CMaterialReqHeaderDetailsResponseDTO;
import com.application.mrmason.dto.CommonMaterialRequestDto;
import com.application.mrmason.dto.ResponseCMaterialReqHeaderDetailsDto;

import com.application.mrmason.entity.CMaterialReqHeaderDetailsEntity;
import com.application.mrmason.entity.CMaterialRequestHeaderEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.CMaterialReqHeaderDetailsRepository;
import com.application.mrmason.repository.CMaterialRequestHeaderRepository;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.CMaterialReqHeaderDetailsService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CMaterialReqHeaderDetailsServiceImpl implements CMaterialReqHeaderDetailsService {

    @Autowired
    private CMaterialRequestHeaderRepository headerRepo;

    @Autowired
    private CustomerRegistrationRepo customerRegistrationRepo;

    @Autowired
    private CMaterialReqHeaderDetailsRepository detailsRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserDAO userDAO;

    @Transactional
    @Override
    public ResponseCMaterialReqHeaderDetailsDto addMaterialRequest(CommonMaterialRequestDto request) {
        List<CMaterialReqHeaderDetailsResponseDTO> responseList = new ArrayList<>();
        ResponseCMaterialReqHeaderDetailsDto response = new ResponseCMaterialReqHeaderDetailsDto();

        try {
            String materialCategory = request.getMaterialCategory();
            String requestedBy = request.getRequestedBy();
            LocalDate deliveryDate = request.getDeliveryDate();
            String deliveryLocation = request.getDeliveryLocation();

            String generatedRequestId = generateRequestId();
            int totalQty = 0;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            boolean isCustomer = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_EC"));
            boolean isServicePerson = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_Developer"));

            if (!isCustomer && !isServicePerson) {
                throw new AccessDeniedException("Unauthorized role");
            }

            List<CMaterialReqHeaderDetailsDTO> requestDTOList = request.getMaterialRequests();
            for (int i = 0; i < requestDTOList.size(); i++) {
                CMaterialReqHeaderDetailsDTO requestDTO = requestDTOList.get(i);
                try {
                    String lineItemId = generateLineId(generatedRequestId, i + 1);
                    log.info("Generated Line Item ID: {}", lineItemId);

                    CMaterialReqHeaderDetailsEntity entity = mapToEntity(requestDTO);

                    entity.setCMatRequestId(generatedRequestId);
                    entity.setCMatRequestIdLineid(lineItemId);
                    entity.setMaterialCategory(materialCategory);
                    entity.setRequestedBy(requestedBy);
                    entity.setOrderDate(LocalDate.now());
                    entity.setUpdatedDate(LocalDate.now());

                    CMaterialReqHeaderDetailsEntity savedEntity = detailsRepo.save(entity);
                    responseList.add(mapToResponseDTO(savedEntity));

                    totalQty += requestDTO.getQty();

                } catch (Exception e) {
                    log.error("Error processing material request for item {}: {}", requestDTO.getItemName(), e);
                    responseList.add(buildErrorResponse(requestDTO));
                }
            }

            CMaterialRequestHeaderEntity headerEntity;
            if (isCustomer) {
                headerEntity = createCustomerHeaderEntity(generatedRequestId, totalQty, requestedBy, deliveryDate,
                        deliveryLocation);
            } else {
                headerEntity = createServicePersonHeaderEntity(generatedRequestId, totalQty, requestedBy, deliveryDate,
                        deliveryLocation);
            }
            headerRepo.save(headerEntity);

            sendMaterialRequestEmail(headerEntity);

            response.setStatus(!responseList.isEmpty());
            response.setMessage("Material request details processed successfully.");
            response.setMaterialRequestDetailsList(responseList);

        } catch (Exception e) {
            log.error("Error processing material requests: {}", e.getMessage(), e);
            response.setStatus(false);
            response.setMessage("Failed to process material requests.");
        }

        return response;
    }

    private CMaterialRequestHeaderEntity createCustomerHeaderEntity(String requestId, int totalQty, String userid,
            LocalDate deliveryDate, String deliveryLocation) {
        CMaterialRequestHeaderEntity headerEntity = new CMaterialRequestHeaderEntity();
        headerEntity.setMaterialRequestId(requestId);
        headerEntity.setTotalQty(totalQty);
        headerEntity.setCreatedDate(LocalDate.now());
        headerEntity.setDeliveryDate(deliveryDate);
        headerEntity.setDeliveryLocation(deliveryLocation);

        CustomerRegistration customer = customerRegistrationRepo.findByUserid(userid);
        if (customer != null) {
            headerEntity.setRequestedBy(customer.getUserid());
            headerEntity.setCustomerName(customer.getCustomerName());
            headerEntity.setCustomerEmail(customer.getUserEmail());
            headerEntity.setCustomerMobile(customer.getUserMobile());
            headerEntity.setUpdatedBy(customer.getCustomerName());
        } else {
            log.warn("Customer with userId {} not found.", userid);
            headerEntity.setRequestedBy(userid);
        }

        return headerEntity;
    }

    private CMaterialRequestHeaderEntity createServicePersonHeaderEntity(String requestId, int totalQty,
            String bodSeqNo,
            LocalDate deliveryDate, String deliveryLocation) {
        CMaterialRequestHeaderEntity headerEntity = new CMaterialRequestHeaderEntity();
        headerEntity.setMaterialRequestId(requestId);
        headerEntity.setTotalQty(totalQty);
        headerEntity.setCreatedDate(LocalDate.now());
        headerEntity.setDeliveryDate(deliveryDate);
        headerEntity.setDeliveryLocation(deliveryLocation);

        User servicePerson = userDAO.findByBodSeqNo(bodSeqNo);
        if (servicePerson != null) {
            headerEntity.setRequestedBy(servicePerson.getBodSeqNo());
            headerEntity.setCustomerName(servicePerson.getName());
            headerEntity.setCustomerEmail(servicePerson.getEmail());
            headerEntity.setCustomerMobile(servicePerson.getMobile());
            headerEntity.setUpdatedBy(servicePerson.getName());
        } else {
            log.warn("Service person with bodSeqNo {} not found.", bodSeqNo);
            headerEntity.setRequestedBy(bodSeqNo);
        }

        return headerEntity;
    }

    private String generateRequestId() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("CMM%04d%02d%02d%02d%02d%02d",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                now.getHour(), now.getMinute(), now.getSecond());
    }

    private String generateLineId(String requestId, int lineCounter) {
        return String.format("%s_%04d", requestId, lineCounter);
    }

    private CMaterialReqHeaderDetailsResponseDTO buildErrorResponse(CMaterialReqHeaderDetailsDTO requestDTO) {
        CMaterialReqHeaderDetailsResponseDTO errorResponse = new CMaterialReqHeaderDetailsResponseDTO();
        errorResponse.setCMatRequestId("N/A");
        errorResponse.setMaterialCategory(requestDTO.getMaterialCategory());
        errorResponse.setBrand(requestDTO.getBrand());
        errorResponse.setItemName(requestDTO.getItemName());
        errorResponse.setItemSize(requestDTO.getItemSize());
        errorResponse.setQty(requestDTO.getQty());
        errorResponse.setRequestedBy("N/A");
        errorResponse.setUpdatedDate(LocalDate.now());
        errorResponse.setCMatRequestIdLineid("N/A");
        return errorResponse;
    }

    private void sendMaterialRequestEmail(CMaterialRequestHeaderEntity entity) {
        String email = entity.getCustomerEmail();
        if (email != null && !email.isEmpty()) {
            String subject = "Material Request Created";
            String body = String.format(
                    "<html><body>" +
                            "<h3>Hello  %s,</h3>" +
                            "<p>Your material request with ID <b>%s</b> has been created successfully.</p>" +
                            "<p><b>Request Details:</b></p>" +
                            "<ul>" +
                            "<li>Created By: %s</li>" +
                            "<li>Created Date: %s</li>" +
                            "<li>Total Quantity: %s</li>" +
                            "</ul>" +
                            "<p>Thank you for using our service!</p>" +
                            "</body></html>",
                    entity.getCustomerName(), entity.getMaterialRequestId(), entity.getRequestedBy(),
                    entity.getCreatedDate(),
                    entity.getTotalQty());

            try {
                emailService.sendEmail(email, subject, body);
                log.info("Material request creation email sent to: {}", email);
            } catch (Exception e) {
                log.error("Failed to send email notification: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No email address found for customer, skipping email notification.");
        }
    }

    @Transactional
    @Override
    public CMaterialReqHeaderDetailsResponseDTO updateMaterialRequestHeaderDetails(
            String cMatRequestIdLineid, CMaterialReqHeaderDetailsDTO requestDTO) {

        CMaterialReqHeaderDetailsEntity entity = detailsRepo.findById(cMatRequestIdLineid)
                .orElseThrow(() -> new RuntimeException("Material Request Header not found"));

        CMaterialRequestHeaderEntity headerEntity = headerRepo.findByMaterialRequestId(entity.getCMatRequestId());
        if (headerEntity == null) {
            throw new RuntimeException("Material Request Header not found for ID: " + entity.getCMatRequestId());
        }

        entity.setMaterialCategory(requestDTO.getMaterialCategory());
        entity.setBrand(requestDTO.getBrand());
        entity.setItemName(requestDTO.getItemName());
        entity.setItemSize(requestDTO.getItemSize());
        entity.setQty(requestDTO.getQty());
        entity.setRequestedBy(requestDTO.getRequestedBy());
        entity.setUpdatedDate(LocalDate.now());

        CMaterialReqHeaderDetailsEntity updatedEntity = detailsRepo.save(entity);
        if (updatedEntity == null || updatedEntity.getQty() != requestDTO.getQty()) {
            throw new RuntimeException("Failed to update qty in the database.");
        }

        int updatedTotalQty = detailsRepo.findByCMatRequestId(entity.getCMatRequestId())
                .stream()
                .mapToInt(CMaterialReqHeaderDetailsEntity::getQty)
                .sum();

        headerEntity.setTotalQty(updatedTotalQty);
        headerRepo.save(headerEntity);

        return mapToResponseDTO(updatedEntity);
    }

    @Override
    public List<CMaterialReqHeaderDetailsResponseDTO> getAllMaterialRequestHeaderDetails(
            String cMatRequestIdLineid, String cMatRequestId, String materialCategory, String brand, String itemName,
            String itemSize, Integer qty, LocalDate orderDate, String requestedBy, LocalDate updatedDate) {

        try {
            List<CMaterialReqHeaderDetailsEntity> entities = detailsRepo.findMaterialRequestsByFilters(
                    cMatRequestIdLineid, cMatRequestId, materialCategory, brand, itemName, itemSize, qty, orderDate,
                    requestedBy,
                    updatedDate);

            if (entities.isEmpty()) {
                log.warn("No material requests found for the given filters.");
                return new ArrayList<>();
            }

            return entities.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching material requests: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private CMaterialReqHeaderDetailsResponseDTO mapToResponseDTO(CMaterialReqHeaderDetailsEntity entity) {
        CMaterialReqHeaderDetailsResponseDTO responseDTO = new CMaterialReqHeaderDetailsResponseDTO();

        responseDTO.setCMatRequestId(entity.getCMatRequestId());
        responseDTO.setCMatRequestIdLineid(entity.getCMatRequestIdLineid());
        responseDTO.setMaterialCategory(entity.getMaterialCategory());
        responseDTO.setBrand(entity.getBrand());
        responseDTO.setItemName(entity.getItemName());
        responseDTO.setItemSize(entity.getItemSize());
        responseDTO.setQty(entity.getQty());
        responseDTO.setOrderDate(entity.getOrderDate());
        responseDTO.setRequestedBy(entity.getRequestedBy());
        responseDTO.setUpdatedDate(entity.getUpdatedDate());

        return responseDTO;
    }

    private CMaterialReqHeaderDetailsEntity mapToEntity(CMaterialReqHeaderDetailsDTO dto) {
        CMaterialReqHeaderDetailsEntity entity = new CMaterialReqHeaderDetailsEntity();
        entity.setMaterialCategory(dto.getMaterialCategory());
        entity.setBrand(dto.getBrand());
        entity.setItemName(dto.getItemName());
        entity.setItemSize(dto.getItemSize());
        entity.setQty(dto.getQty());
        return entity;
    }
}
