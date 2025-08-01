package com.application.mrmason.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.application.mrmason.entity.CMaterialRequestHeaderEntity;

import jakarta.persistence.criteria.Predicate;

public class CMaterialRequestHeaderSpecification {

    public static Specification<CMaterialRequestHeaderEntity> filterByParams(
            String materialRequestId,
            String customerEmail,
            String customerName,
            String customerMobile,
            String deliveryLocation,
            LocalDate fromRequestDate,
            LocalDate toRequestDate,
            LocalDate fromDeliveryDate,
            LocalDate toDeliveryDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (materialRequestId != null && !materialRequestId.isEmpty()) {
                predicates.add(cb.equal(root.get("materialRequestId"), materialRequestId));
            }
            if (customerEmail != null && !customerEmail.isEmpty()) {
                predicates.add(cb.equal(root.get("customerEmail"), customerEmail));
            }
            if (customerName != null && !customerName.isEmpty()) {
                predicates.add(cb.equal(root.get("customerName"), customerName));
            }
            if (customerMobile != null && !customerMobile.isEmpty()) {
                predicates.add(cb.equal(root.get("customerMobile"), customerMobile));
            }
            if (deliveryLocation != null && !deliveryLocation.isEmpty()) {
                predicates.add(cb.equal(root.get("deliveryLocation"), deliveryLocation));
            }
            if (fromRequestDate != null && toRequestDate != null) {
                predicates.add(cb.between(root.get("createdDate"), fromRequestDate, toRequestDate));
            }
            if (fromDeliveryDate != null && toDeliveryDate != null) {
                predicates.add(cb.between(root.get("deliveryDate"), fromDeliveryDate, toDeliveryDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
