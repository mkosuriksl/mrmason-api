package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.application.mrmason.entity.AdminMembershipPlanEntity;

public interface AdminMembershipPlanRepository extends JpaRepository<AdminMembershipPlanEntity, String> {

        @Query("SELECT p FROM AdminMembershipPlanEntity p WHERE " +
                        "(:membershipPlanId IS NULL OR p.membershipPlanId = :membershipPlanId) AND " +
                        "(:amount IS NULL OR p.amount = :amount) AND " +
                        "(:noOfDaysValid IS NULL OR p.noOfDaysValid = :noOfDaysValid) AND " +
                        "(:planName IS NULL OR p.planName = :planName) AND " +
                        "(:status IS NULL OR p.status = :status) AND " +
                        "(:updatedBy IS NULL OR p.updatedBy = :updatedBy)")
        List<AdminMembershipPlanEntity> findByCriteria(@Param("membershipPlanId") String membershipPlanId,
                        @Param("amount") Integer amount,
                        @Param("noOfDaysValid") String noOfDaysValid,
                        @Param("planName") String planName,
                        @Param("status") String status,
                        @Param("updatedBy") String updatedBy);

}
