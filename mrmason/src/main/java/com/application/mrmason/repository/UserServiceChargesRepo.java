package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.UserServiceCharges;

@Repository
public interface UserServiceChargesRepo extends JpaRepository<UserServiceCharges, String> {

}
