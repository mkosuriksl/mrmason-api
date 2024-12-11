package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.application.mrmason.entity.SPMembRenewHeaderEntity;

import java.util.List;

@Repository
public interface SPMembRenewHeaderRepo extends JpaRepository<SPMembRenewHeaderEntity, String> {

       @Query("SELECT s FROM SPMembRenewHeaderEntity s " +
                     "WHERE (:membershipOrderId IS NULL OR s.membershipOrderId = :membershipOrderId) " +
                     "AND (:orderPlacedBy IS NULL OR s.orderPlacedBy = :orderPlacedBy)")
       List<SPMembRenewHeaderEntity> findByMembershipOrderIdOrOrderPlacedBy(
                     @Param("membershipOrderId") String membershipOrderId,
                     @Param("orderPlacedBy") String orderPlacedBy);
}
