package com.application.mrmason.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.SPMembRenewDetailsEntity;

@Repository
public interface SPMembRenewDetailsRepository extends JpaRepository<SPMembRenewDetailsEntity, String> {

       @Query("SELECT s FROM SPMembRenewDetailsEntity s WHERE " +
                     "(:membershipOrderIdLineItem IS NULL OR s.membershipOrderIdLineItem LIKE %:membershipOrderIdLineItem%) AND "
                     +
                     "(:membershipOrderId IS NULL OR s.membershipOrderId = :membershipOrderId) AND " +
                     "(:orderAmount IS NULL OR s.orderAmount = :orderAmount) AND " +
                     "(:orderDate IS NULL OR s.orderDate = :orderDate) AND " +
                     "(:orderPlacedBy IS NULL OR s.orderPlacedBy = :orderPlacedBy) AND " +
                     "(:planId IS NULL OR s.planId = :planId) AND " +
                     "(:status IS NULL OR s.status = :status) AND " +
                     "(:storeId IS NULL OR s.storeId = :storeId)")
       List<SPMembRenewDetailsEntity> findMembershipRenewalDetails(
                     String membershipOrderIdLineItem,
                     String membershipOrderId,
                     Integer orderAmount,
                     LocalDateTime orderDate,
                     String orderPlacedBy,
                     String planId,
                     String status,
                     String storeId);

       List<SPMembRenewDetailsEntity> findByMembershipOrderIdLineItem(String membershipOrderIdLineItem);

       List<SPMembRenewDetailsEntity> findByMembershipOrderId(String membershipOrderId);

}
