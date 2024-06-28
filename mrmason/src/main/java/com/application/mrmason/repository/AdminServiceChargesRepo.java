package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminServiceCharges;

@Repository
public interface AdminServiceChargesRepo extends JpaRepository<AdminServiceCharges, String> {

}
