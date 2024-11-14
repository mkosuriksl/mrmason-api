package com.application.mrmason.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.application.mrmason.dto.AdminMembershipGetResponseDTO;
import com.application.mrmason.dto.AdminMembershipPlanDTO;
import com.application.mrmason.dto.AdminMembershipPlanResponseDTO;
import com.application.mrmason.service.AdminMembershipPlanService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@PreAuthorize("hasAuthority('Adm')")
public class AdminMembershipPlanController {

        @Autowired
        private AdminMembershipPlanService membershipPlanService;

        @PostMapping("/add-admin-membership-plan")
        public ResponseEntity<AdminMembershipPlanResponseDTO> addMembershipPlan(
                        @RequestBody AdminMembershipPlanDTO memPlanDTO) {
                log.info("Adding new membership plan: {}", memPlanDTO);
                AdminMembershipPlanDTO addedPlan = membershipPlanService.addMembershipPlan(memPlanDTO);
                return ResponseEntity.ok(new AdminMembershipPlanResponseDTO("Admin Added Membership Plan successfully",
                                "Success", addedPlan));
        }

        @PutMapping("/update-admin-membership-plan")
        public ResponseEntity<AdminMembershipPlanResponseDTO> updateMembershipPlan(
                        @RequestBody AdminMembershipPlanDTO membershipPlanDTO) {
                log.info("Updating membership plan with ID: {}", membershipPlanDTO.getMembershipPlanId());
                AdminMembershipPlanDTO updatedPlan = membershipPlanService
                                .updateMembershipPlan(membershipPlanDTO.getMembershipPlanId(), membershipPlanDTO);
                return ResponseEntity.ok(new AdminMembershipPlanResponseDTO(
                                "Admin updated the Membership Plan successfully", "Success", updatedPlan));
        }

        @PreAuthorize("hasAnyAuthority('Adm', 'Developer')")
        @GetMapping("/get-admin-membership-plan")
        public ResponseEntity<AdminMembershipGetResponseDTO> getMembershipPlan(
                        @RequestParam(value = "membershipPlanId", required = false) String membershipPlanId,
                        @RequestParam(value = "amount", required = false) Integer amount,
                        @RequestParam(value = "noOfDaysValid", required = false) String noOfDaysValid,
                        @RequestParam(value = "planName", required = false) String planName,
                        @RequestParam(value = "status", required = false) String status,
                        @RequestParam(value = "updatedBy", required = false) String updatedBy) {

                log.info("Retrieving membership plans with filters");
                List<AdminMembershipPlanDTO> membershipPlans = membershipPlanService.getMembershipPlan(
                                membershipPlanId, amount, noOfDaysValid, planName, status, updatedBy);

                String message = membershipPlans.isEmpty() ? "No membership plans are available"
                                : "Admin Membership Plans Retrieved successfully";
                return ResponseEntity.ok(new AdminMembershipGetResponseDTO(message,
                                String.valueOf(!membershipPlans.isEmpty()), membershipPlans));
        }
}
