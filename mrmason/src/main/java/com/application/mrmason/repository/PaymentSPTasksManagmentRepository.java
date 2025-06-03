package com.application.mrmason.repository;

import com.application.mrmason.entity.PaymentSPTasksManagment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentSPTasksManagmentRepository extends JpaRepository<PaymentSPTasksManagment, String> {
    Optional<PaymentSPTasksManagment> findByRequestLineId(String requestLineId);
}
