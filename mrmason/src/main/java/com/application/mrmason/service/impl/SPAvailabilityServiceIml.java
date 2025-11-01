package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.application.mrmason.entity.SPAvailability;
import com.application.mrmason.entity.SPAvailabilityHistory;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.SPAvailabilityHistoryRepository;
import com.application.mrmason.repository.SPAvailabilityRepo;
import com.application.mrmason.repository.UserDAO;

@Service
public class SPAvailabilityServiceIml {

	@Autowired
	SPAvailabilityRepo availabilityReo;
	
	@Autowired
	private SPAvailabilityHistoryRepository availabilityHistoryRepository;

	@Autowired
	UserDAO userDAO;

	public SPAvailability availability(SPAvailability available) {
        Optional<User> userExists = Optional.ofNullable(userDAO.findByBodSeqNo(available.getBodSeqNo()));

        if (userExists.isPresent()) {
            User userDb = userExists.get();

            if (available.getBodSeqNo() != null && userDb.getStatus().equalsIgnoreCase("Active")) {

                Optional<SPAvailability> existingOpt = availabilityReo.findByBodSeqNos(available.getBodSeqNo());

                if (existingOpt.isPresent()) {
                    // Move old record to history table
                    SPAvailability existing = existingOpt.get();

                    SPAvailabilityHistory history = new SPAvailabilityHistory();
                    history.setBodSeqNo(existing.getBodSeqNo());
                    history.setAvailability(existing.getAvailability());
                    history.setAddress(existing.getAddress());
                    history.setDateTimeOfUpdate(existing.getDateTimeOfUpdate());

                    availabilityHistoryRepository.save(history);

                    // Update existing record
                    existing.setAvailability(available.getAvailability());
                    existing.setAddress(available.getAddress());
                    existing.setDateTimeOfUpdate(LocalDateTime.now().toString());
                    existing.setUpdatedDate(LocalDateTime.now());
                    return availabilityReo.save(existing);

                } else {
                    // New entry
                    return availabilityReo.save(available);
                }
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
