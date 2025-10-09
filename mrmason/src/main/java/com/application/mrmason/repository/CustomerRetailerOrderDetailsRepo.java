package com.application.mrmason.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.CustomerRetailerOrderDetailsEntity;

public interface CustomerRetailerOrderDetailsRepo extends JpaRepository<CustomerRetailerOrderDetailsEntity, String> {

}
