package com.applicaion.mrmason.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.applicaion.mrmason.dto.AddServiceGetDto;
import com.applicaion.mrmason.entity.AddServices;
import com.applicaion.mrmason.entity.User;
import com.applicaion.mrmason.repository.AddServiceRepo;
import com.applicaion.mrmason.repository.UserDAO;

@Service
public class AddServicesServiceIml {

	@Autowired
	AddServiceRepo repo;

	@Autowired
	UserDAO userDAO;

	@Autowired
	UserService userService;

	
	public AddServices addServicePerson(AddServices add, String bodSeqNo) throws Exception {
		Optional<User> optionalUser = userDAO.findById(bodSeqNo);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			return repo.save(add);
		} else {

			throw new Exception("User not found for bodSeqNo: " + bodSeqNo);
		}

	}


	public AddServices updateAddServiceDetails(AddServiceGetDto services, String userIdServiceId, String serviceSubCategory, String bodSeqNo) {
		Optional<AddServices> optionalAdd = Optional.of(repo.findByUserIdServiceId(userIdServiceId));
		if (optionalAdd.isPresent()) {
			AddServices add = optionalAdd.get();
			add.setServiceId(services.getServiceId());
			add.setStatus(services.getStatus());
			return repo.save(add);
		}
		return null;
	}

	
	
	public List<AddServices> getPerson(String bodSeqNo, String serviceSubCategory, String useridServiceId) {

		if (bodSeqNo == null && serviceSubCategory != null && useridServiceId == null) {
			List<AddServices> user = repo.findByServiceSubCategory(serviceSubCategory);
			return user;
		} else if (bodSeqNo != null && serviceSubCategory == null && useridServiceId == null) {

			List<AddServices> user = repo.findByBodSeqNo(bodSeqNo);
			return user;
		} else if (bodSeqNo == null && serviceSubCategory == null && useridServiceId != null) {
			List<AddServices> user = repo.getUserIdServiceIdDetails(useridServiceId);
			return user;
		}
		return null;

	}

}
