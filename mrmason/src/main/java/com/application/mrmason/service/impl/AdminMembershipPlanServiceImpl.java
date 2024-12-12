package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.mrmason.dto.AdminMembershipPlanDTO;
import com.application.mrmason.entity.AdminMembershipPlanEntity;
import com.application.mrmason.repository.AdminMembershipPlanRepository;
import com.application.mrmason.service.AdminMembershipPlanService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminMembershipPlanServiceImpl implements AdminMembershipPlanService {

    @Autowired
    private AdminMembershipPlanRepository membershipPlanRepository;

    @Override
    public AdminMembershipPlanDTO addMembershipPlan(AdminMembershipPlanDTO memPlanDTO) {
        log.info("Saving new membership plan to the database: {}", memPlanDTO);
        AdminMembershipPlanEntity memPlan = mapToEntity(memPlanDTO);
        AdminMembershipPlanEntity savedMemPlan = membershipPlanRepository.save(memPlan);
        return mapToDto(savedMemPlan);
    }

    @Override
    public AdminMembershipPlanDTO updateMembershipPlan(String membershipPlanId, AdminMembershipPlanDTO membPlanDTO) {
        AdminMembershipPlanEntity memPlan = membershipPlanRepository.findById(membershipPlanId)
                .orElseThrow(() -> new RuntimeException("Membership plan not found"));
        updateEntityFields(memPlan, membPlanDTO);
        membershipPlanRepository.save(memPlan);
        return mapToDto(memPlan);
    }

    @Override
    public List<AdminMembershipPlanDTO> getMembershipPlan(String membershipPlanId, Integer amount,
            String noOfDaysValid, String planName, String status, String defaultPlan, String updatedBy) {
        log.info("Fetching membership plans with filters");

        return membershipPlanRepository.findByCriteria(
                membershipPlanId, amount, noOfDaysValid, planName, status, defaultPlan, updatedBy)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private AdminMembershipPlanEntity mapToEntity(AdminMembershipPlanDTO dto) {
        AdminMembershipPlanEntity entity = new AdminMembershipPlanEntity();
        entity.setMembershipPlanId(dto.getMembershipPlanId());
        entity.setAmount(dto.getAmount());
        entity.setNoOfDaysValid(dto.getNoOfDaysValid());
        entity.setPlanName(dto.getPlanName());
        entity.setStatus(dto.getStatus());
        entity.setDefaultPlan(dto.getDefaultPlan());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setUpdatedDate(LocalDateTime.now());
        return entity;
    }

    private void updateEntityFields(AdminMembershipPlanEntity entity, AdminMembershipPlanDTO dto) {
        entity.setAmount(dto.getAmount());
        entity.setNoOfDaysValid(dto.getNoOfDaysValid());
        entity.setPlanName(dto.getPlanName());
        entity.setStatus(dto.getStatus());
        entity.setDefaultPlan(dto.getDefaultPlan());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setUpdatedDate(LocalDateTime.now());
    }

    private AdminMembershipPlanDTO mapToDto(AdminMembershipPlanEntity entity) {
        AdminMembershipPlanDTO dto = new AdminMembershipPlanDTO();
        dto.setMembershipPlanId(entity.getMembershipPlanId());
        dto.setAmount(entity.getAmount());
        dto.setNoOfDaysValid(entity.getNoOfDaysValid());
        dto.setPlanName(entity.getPlanName());
        dto.setStatus(entity.getStatus());
        dto.setDefaultPlan(entity.getDefaultPlan());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedDate(entity.getUpdatedDate());
        return dto;
    }
}
