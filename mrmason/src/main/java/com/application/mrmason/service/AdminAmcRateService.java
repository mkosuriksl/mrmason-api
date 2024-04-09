package com.application.mrmason.service;

import java.util.List;
import com.application.mrmason.entity.AdminAmcRate;

public interface AdminAmcRateService {
	AdminAmcRate addAdminamc(AdminAmcRate amc);
	List<AdminAmcRate> getAmcRates(AdminAmcRate amc);
	AdminAmcRate updateAmcRates(AdminAmcRate amc);
}

