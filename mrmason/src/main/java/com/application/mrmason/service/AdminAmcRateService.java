package com.application.mrmason.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.AdminAmcRate;

public interface AdminAmcRateService {
	AdminAmcRate addAdminamc(AdminAmcRate amc);
	Page<AdminAmcRate> getAmcRates(String amcId,String planId,String assetSubCat,String assetModel,String assetBrand,Pageable pageable);
	AdminAmcRate updateAmcRates(AdminAmcRate amc);
}

