package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.AdminMembershipPlanDTO;

public interface AdminMembershipPlanService {

    AdminMembershipPlanDTO addMembershipPlan(AdminMembershipPlanDTO membershipPlanDTO);

    AdminMembershipPlanDTO updateMembershipPlan(String membershipPlanId, AdminMembershipPlanDTO membershipPlanDTO);

    List<AdminMembershipPlanDTO> getMembershipPlan(
            String membershipPlanId, Integer amount, String noOfDaysValid, String planName, String status,
            String defaultPlan,
            String updatedBy);

}
