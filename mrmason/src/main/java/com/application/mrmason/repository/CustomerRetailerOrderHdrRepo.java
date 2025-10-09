package com.application.mrmason.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;
import com.application.mrmason.enums.OrderStatus;

public interface CustomerRetailerOrderHdrRepo extends JpaRepository<CustomerRetailerOrderHdrEntity, String> {

    Optional<CustomerRetailerOrderHdrEntity> findByOrderId(String orderId);

    List<CustomerRetailerOrderHdrEntity> findByCustomerId(String cId);

    List<CustomerRetailerOrderHdrEntity> findByCustomerIdIn(List<String> customerIdList);

	List<CustomerRetailerOrderHdrEntity> findByOrderDateBetween(LocalDate fromOrderDate, LocalDate toOrderDate);

	List<CustomerRetailerOrderHdrEntity> findByOrderStatus(OrderStatus orderStatus);

	List<CustomerRetailerOrderHdrEntity> findByRetailerId(String retailerId);
}
