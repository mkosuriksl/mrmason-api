package com.application.mrmason.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.SPMembRenewHeaderRequestDTO;
import com.application.mrmason.dto.SPMembRenewHeaderResponseDTO;

import com.application.mrmason.entity.SPMembRenewHeaderEntity;
import com.application.mrmason.entity.User;

import com.application.mrmason.repository.SPMembRenewHeaderRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.SPMembRenewHeaderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SPMembRenewHeaderServiceImpl implements SPMembRenewHeaderService {

    @Autowired
    private SPMembRenewHeaderRepo repo;

    @Autowired
    private UserDAO userDAO;

    @Override
    public SPMembRenewHeaderResponseDTO addMembershipOrder(SPMembRenewHeaderRequestDTO dto) {
        log.info("Attempting to add membership order with ID: {}", dto.getMembershipOrderId());

        User user = userDAO.findByBodSeqNo(dto.getOrderPlacedBy());
        if (user == null) {
            log.error("User not found with bodSeqNo: {}", dto.getOrderPlacedBy());
            throw new RuntimeException("User not found with bodSeqNo: " + dto.getOrderPlacedBy());
        }

        SPMembRenewHeaderEntity entity = new SPMembRenewHeaderEntity();
        entity.setMembershipOrderId(dto.getMembershipOrderId());
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderPlacedBy(dto.getOrderPlacedBy());
        entity.setStatus(dto.getStatus());

        SPMembRenewHeaderEntity savedEntity = repo.save(entity);
        log.info("Membership order saved with ID: {}", savedEntity.getMembershipOrderId());

        return mapToResponseDTO(savedEntity);
    }

    @Override
    public SPMembRenewHeaderResponseDTO updateMembershipOrder(String membershipOrderId,
            SPMembRenewHeaderRequestDTO dto) {
        log.info("Updating membership order with ID: {}", membershipOrderId);

        SPMembRenewHeaderEntity entity = repo.findById(membershipOrderId).orElse(new SPMembRenewHeaderEntity());

        entity.setMembershipOrderId(membershipOrderId);
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderPlacedBy(dto.getOrderPlacedBy());
        entity.setStatus(dto.getStatus());

        SPMembRenewHeaderEntity updatedEntity = repo.save(entity);
        log.info("Membership order updated with ID: {}", updatedEntity.getMembershipOrderId());

        return mapToResponseDTO(updatedEntity);
    }

    @Override
    public List<SPMembRenewHeaderResponseDTO> getAllMembershipOrders(String membershipOrderId, String orderPlacedBy) {
        log.info("Fetching all membership orders with filters - membershipOrderId: {}, orderPlacedBy: {}",
                membershipOrderId, orderPlacedBy);

        List<SPMembRenewHeaderEntity> entities = repo.findByMembershipOrderIdOrOrderPlacedBy(membershipOrderId,
                orderPlacedBy);

        return entities.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMembershipOrder(String membershipOrderId) {
        log.info("Deleting membership order with ID: {}", membershipOrderId);
        repo.deleteById(membershipOrderId);
    }

    private SPMembRenewHeaderResponseDTO mapToResponseDTO(SPMembRenewHeaderEntity entity) {
        SPMembRenewHeaderResponseDTO responseDTO = new SPMembRenewHeaderResponseDTO();
        responseDTO.setMembershipOrderId(entity.getMembershipOrderId());
        responseDTO.setOrderAmount(entity.getOrderAmount());
        responseDTO.setOrderDate(entity.getOrderDate());
        responseDTO.setOrderPlacedBy(entity.getOrderPlacedBy());
        responseDTO.setStatus(entity.getStatus());
        return responseDTO;
    }
}