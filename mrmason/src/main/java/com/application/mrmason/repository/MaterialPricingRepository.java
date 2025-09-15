package com.application.mrmason.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.application.mrmason.entity.MaterialPricing;

public interface MaterialPricingRepository extends JpaRepository<MaterialPricing, String> {

	List<MaterialPricing> findByUserIdSkuIn(List<String> skuList);
}
