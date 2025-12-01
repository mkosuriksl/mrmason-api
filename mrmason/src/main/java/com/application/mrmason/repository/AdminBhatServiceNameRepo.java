package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.AdminBhatServiceName;

@Repository
public interface AdminBhatServiceNameRepo extends JpaRepository<AdminBhatServiceName, String> {
    Page<AdminBhatServiceName> findByServiceIdOrServiceNameOrServiceSubCategoryOrderByAddedDateDesc(
            String serviceId, String serviceName, String serviceSubCat, Pageable pageable);

    AdminBhatServiceName findByServiceId(String serviceId);

    List<AdminBhatServiceName> findByServiceIdIn(List<String> serviceIds);

}
