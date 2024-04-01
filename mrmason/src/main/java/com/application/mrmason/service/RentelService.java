package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.RentalDto;
import com.application.mrmason.entity.Rentel;

public interface RentelService {
	Rentel addRentalReq(Rentel rent);
	List<Rentel> getRentalReq(RentalDto rent);
	Rentel updateRentalReq(Rentel rent);
}
