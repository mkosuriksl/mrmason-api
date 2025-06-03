package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminSecurityEntity;

import java.util.Optional;

@Repository
public interface AdminSecurityRepository extends JpaRepository<AdminSecurityEntity,Integer> {


    Optional<AdminSecurityEntity> findByAwsAccessKey(String accessKey);
}
