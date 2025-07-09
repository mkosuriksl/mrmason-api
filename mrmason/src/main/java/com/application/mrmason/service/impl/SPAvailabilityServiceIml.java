package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.SPAvailability;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.SPAvailabilityRepo;
import com.application.mrmason.repository.UserDAO;

@Service
public class SPAvailabilityServiceIml {

	@Autowired
	SPAvailabilityRepo availabilityReo;

	@Autowired
	UserDAO userDAO;

	public SPAvailability availability(SPAvailability available) {

		Optional<User> userExists = Optional.ofNullable(userDAO.findByBodSeqNo(available.getBodSeqNo()));
		if (userExists.isPresent()) {

			User userDb = userExists.get();
			if (available.getBodSeqNo() != null && userDb.getStatus().equalsIgnoreCase("Active")) {

				return availabilityReo.save(available);
			}
		}
		return null;

	}

	public List<SPAvailability> getAvailabilitys(String bodSeqNo) {
		Optional<User> userExists = userDAO.findById(bodSeqNo);

		if (userExists.isPresent()) {
			Optional<List<SPAvailability>> bodSeqNoExists = Optional
					.ofNullable(availabilityReo.findByBodSeqNo(userExists.get().getBodSeqNo()));
			if (!bodSeqNoExists.get().isEmpty()) {
				return bodSeqNoExists.get();
			} else {
				return null;
			}

		}
		return null;
	}
	
	public Page<SPAvailability> getAvailability(String bodSeqNo, int page, int size) {
	    Optional<User> userExists = userDAO.findById(bodSeqNo);

	    if (userExists.isPresent()) {
	        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
	        return availabilityReo.findByBodSeqNo(userExists.get().getBodSeqNo(), pageable);
	    }
	    return Page.empty();
	}


}
