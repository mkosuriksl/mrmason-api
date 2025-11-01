package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CustomerRegistrationDto;
import com.application.mrmason.dto.CustomerResponseDTO;
import com.application.mrmason.dto.ResponseLoginDto;
import com.application.mrmason.entity.CustomerEmailOtp;
import com.application.mrmason.entity.CustomerLogin;
import com.application.mrmason.entity.CustomerMobileOtp;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.MessageTemplate;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.CustomerEmailOtpRepo;
import com.application.mrmason.repository.CustomerLoginRepo;
import com.application.mrmason.repository.CustomerMobileOtpRepo;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.MessageTemplateRepository;
import com.application.mrmason.security.JwtService;
import com.application.mrmason.service.CustomerRegistrationService;
import com.application.mrmason.service.EmailService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


@Service
public class CustomerRegistrationServiceImpl implements CustomerRegistrationService {

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	CustomerEmailOtpRepo emailRepo;
	@Autowired
	CustomerMobileOtpRepo mobileRepo;
	@Autowired
	CustomerRegistrationRepo repo;
	@Autowired
    CustomerLoginRepo loginRepo;
	@Autowired
	BCryptPasswordEncoder byCrypt;
	@Autowired
	JwtService jwtService;
	@Autowired
	CustomerRegistrationRepo customerRegistrationRepo;
	@Autowired
	private MessageTemplateRepository messageTemplateRepository;
	@Autowired
	private EmailService mailService;
	@Autowired
	private SmsService smsService;

	@Override
	public CustomerRegistrationDto saveData(CustomerRegistration customer) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
		String encryptPassword = byCrypt.encode(customer.getUserPassword());
		customer.setUserPassword(encryptPassword);
		CustomerRegistration registration= repo.save(customer);

		CustomerLogin loginEntity = new CustomerLogin();
		loginEntity.setUserEmail(customer.getUserEmail());
		loginEntity.setUserMobile(customer.getUserMobile());
		loginEntity.setUserPassword(customer.getUserPassword());
		loginEntity.setMobileVerified("no");
		loginEntity.setEmailVerified("no");
		loginEntity.setStatus("inactive");
		loginEntity.setRegSource(customer.getRegSource());
		loginRepo.save(loginEntity);

		CustomerEmailOtp emailLoginEntity = new CustomerEmailOtp();
		emailLoginEntity.setEmail(customer.getUserEmail());
		emailLoginEntity.setRegSource(customer.getRegSource());
		emailRepo.save(emailLoginEntity);
		
		CustomerMobileOtp mobileLoginEntity = new CustomerMobileOtp();
		mobileLoginEntity.setMobileNum(customer.getUserMobile());
		mobileLoginEntity.setRegSource(customer.getRegSource());
		mobileRepo.save(mobileLoginEntity);
		

		CustomerRegistrationDto customerDto = new CustomerRegistrationDto();
		customerDto.setId(customer.getId());
		customerDto.setUserName(customer.getUsername());
		customerDto.setUserEmail(customer.getUserEmail());
		customerDto.setUserid(customer.getUserid());
		customerDto.setUserMobile(customer.getUserMobile());
		customerDto.setRegDate(customer.getRegDate());
		customerDto.setUserPincode(customer.getUserPincode());
		customerDto.setUserState(customer.getUserState());
		customerDto.setUserTown(customer.getUserTown());
		customerDto.setUsertype(String.valueOf(registration.getUserType()));
		customerDto.setUserDistrict(customer.getUserDistrict());
		customerDto.setRegSource(customer.getRegSource());
		return customerDto;
	}

	@Override
	public boolean isUserUnique(CustomerRegistration customer) {
		CustomerRegistration user = repo.findUserWithSameRegSource(customer.getUserEmail(), customer.getUserMobile(),customer.getRegSource());
		return user == null;
	}

	@Override
//	public List<CustomerRegistration> getCustomerData(String userEmail, String userMobile, String userState, String fromDate,
//			String toDate) {
//		if (fromDate == null && toDate == null && userEmail != null || userMobile != null || userState != null) {
//			return repo.findAllByUserEmailOrUserMobileOrUserState(userEmail, userMobile, userState);
//		} else {
//			return repo.findByRegDateBetween(fromDate, toDate);
//		}
//	}
	
	 public List<CustomerResponseDTO> getCustomerData(
	            String userEmail, String userMobile, String userState, String fromDate,
	            String toDate,
	            Map<String, String> requestParams) {

	        List<String> expectedParams = Arrays.asList(
	                "userEmail", "userMobile", "userState", "fromDate", "toDate"
	        );

	        for (String paramName : requestParams.keySet()) {
	            if (!expectedParams.contains(paramName)) {
	                throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
	            }
	        }

	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<CustomerRegistration> query = cb.createQuery(CustomerRegistration.class);
	        Root<CustomerRegistration> root = query.from(CustomerRegistration.class);

	        List<Predicate> predicates = new ArrayList<>();

	        if (userEmail != null) {
	            predicates.add(cb.equal(root.get("userEmail"), userEmail));
	        }
	        if (userMobile != null) {
	            predicates.add(cb.equal(root.get("userMobile"), userMobile));
	        }
	        if (userState != null) {
	            predicates.add(cb.equal(root.get("userState"), userState));
	        }
	        if (fromDate != null && toDate != null) {
				predicates.add(cb.between(root.get("regDate"), fromDate, toDate));
			} else if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("regDate"), fromDate));
			} else if (toDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("regDate"), toDate));
			}
	        query.where(predicates.toArray(new Predicate[0]));

	        List<CustomerRegistration> users = entityManager.createQuery(query).getResultList();

	        return users.stream().map(this::convertToDto).collect(Collectors.toList());
	    }

	    private CustomerResponseDTO convertToDto(CustomerRegistration user) {
	    	CustomerResponseDTO dto = new CustomerResponseDTO();
	        dto.setId(user.getId());
	        dto.setUserid(user.getUserid());
	        dto.setUserEmail(user.getUserEmail());
	        dto.setUserMobile(user.getUserMobile());
	        dto.setUserType(user.getUserType());
	        dto.setUserName(user.getUsername());
	        dto.setUserTown(user.getUserTown());
	        dto.setUserDistrict(user.getUserDistrict());
	        dto.setUserState(user.getUserState());
	        dto.setUserPincode(user.getUserPincode());
	        dto.setRegDate(user.getRegDate());
	        return dto;
	    
	}

	@Override
	public String updateCustomerData(String userName, String userTown, String userState, String userDist,
			String userPinCode, String userid) {
		Optional<CustomerRegistration> existedById = Optional.of(repo.findByUserid(userid));
		if (existedById.isPresent()) {
			existedById.get().setUserName(userName);
			existedById.get().setUserPincode(userPinCode);
			existedById.get().setUserState(userState);
			existedById.get().setUserTown(userTown);
			existedById.get().setUserDistrict(userDist);
			repo.save(existedById.get());
			return "Success";
		} else {
			return null;
		}
	}

	@Override
	public CustomerRegistration getCustomer(String email, String phno) {
		return repo.findByUserEmailOrUserMobile(email, phno);
	}

	public CustomerRegistrationDto getProfileData(String userid) {
		CustomerRegistrationDto customerDto = new CustomerRegistrationDto();
		Optional<CustomerRegistration> user = Optional.of(repo.findByUserid(userid));
		customerDto.setId(user.get().getId());
		customerDto.setRegDate(user.get().getRegDate());
		customerDto.setUserDistrict((user.get().getUserDistrict()));
		customerDto.setUserEmail(user.get().getUserEmail());
		customerDto.setUserid(user.get().getUserid());
		customerDto.setUserMobile(user.get().getUserMobile());
		customerDto.setUserName(user.get().getUsername());
		customerDto.setUserPincode(user.get().getUserPincode());
		customerDto.setUserState(user.get().getUserState());
		customerDto.setUserTown(user.get().getUserTown());
		customerDto.setUsertype(String.valueOf(user.get().getUserType()));
		customerDto.setRegSource(user.get().getRegSource());
		return customerDto;
	}

	@Override
	public String changePassword(String usermail, String oldPass, String newPass, String confPass, String phno,RegSource regSource) {
		BCryptPasswordEncoder byCrypt = new BCryptPasswordEncoder();
		Optional<CustomerLogin> user = Optional.of(loginRepo.findByUserEmailOrUserMobileAndRegSource(usermail, phno,regSource));
		if (user.isPresent()) {
			if (byCrypt.matches(oldPass, user.get().getUserPassword())) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					user.get().setUserPassword(encryptPassword);
					loginRepo.save(user.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else {
			return "invalid";
		}

	}

	@Override
	public ResponseLoginDto loginDetails(String userEmail, String phno, String userPassword,RegSource regSource) {
		ResponseLoginDto response = new ResponseLoginDto();

		CustomerLogin loginDb = loginRepo.findByUserEmailOrUserMobileAndRegSource(userEmail, phno,regSource);
		if (loginDb != null) {
			if (loginDb.getStatus().equalsIgnoreCase("active")) {
				CustomerRegistration customerRegistration = customerRegistrationRepo
						.findByUserEmailAndRegSource(loginDb.getUserEmail(),loginDb.getRegSource());
				if (userEmail != null && phno == null) {
					if (loginDb.getEmailVerified().equalsIgnoreCase("yes")) {
						if (byCrypt.matches(userPassword, loginDb.getUserPassword())) {
							String jwtToken = jwtService.generateToken(customerRegistration,customerRegistration.getUserid());
							response.setMessage("Login Successful.");
							response.setJwtToken(jwtToken);
							response.setStatus(true);
							response.setLoginDetails(getProfileData(customerRegistration.getUserid()));
							return response;

						} else {
							response.setMessage("Invalid Password");
							return response;
						}
					} else {
						response.setMessage("verify Email");
						return response;
					}
				} else if (userEmail == null && phno != null) {
					if (loginDb.getMobileVerified().equalsIgnoreCase("yes")) {
						CustomerRegistration user=repo.findByUserMobileAndRegSource(phno,regSource);
						if (byCrypt.matches(userPassword, loginDb.getUserPassword())) {
							String jwtToken = jwtService.generateToken(customerRegistration,customerRegistration.getUserid());
							response.setJwtToken(jwtToken);
							response.setMessage("Login Successful.");
							response.setStatus(true);
							response.setLoginDetails(getProfileData(user.getUserid()));
							return response;
						} else {
							response.setMessage("Invalid Password");
							return response;
						}
					} else {
						response.setMessage("verify Mobile");
						return response;
					}
				}
			} else {
				response.setMessage("Inactive User");
				return response;
			}
		}

		response.setMessage("Invalid User.!");
		return response;
	}
	
	@Override
	public void sendPromotionalNotifications(String userPincode, RegSource regSource) {
	    // 1. Load template
	    MessageTemplate template = messageTemplateRepository
	            .findByTemplateCode("PROMO_OFFER")
	            .orElseThrow(() -> new RuntimeException("Template not found!"));

	    // 2. Get recipients
	    List<CustomerRegistration> customers;

	    if (userPincode == null || userPincode.isBlank()) {
	        // No location provided → send to all
	        customers = customerRegistrationRepo.findAll();
	    } else {
	        // Try customers in that location
	        customers = customerRegistrationRepo.findByUserPincode(userPincode);
	        if (customers.isEmpty()) {
	            // Location not found → fallback to all
	            customers = customerRegistrationRepo.findAll();
	        }
	    }

	    // 3. Send messages
	    for (CustomerRegistration customer : customers) {
	        String message = template.getTemplateText()
	                .replace("{cId}", safeValue(customer.getUserid()))
	                .replace("{name}", safeValue(customer.getUsername()))
	                .replace("{location}", safeValue(customer.getUserPincode()));

	        String subject = "Promotional Offer"; // could also be read from template

	        // Send Email
	        if (customer.getUserEmail() != null && !customer.getUserEmail().isBlank()) {
	            mailService.sendEmailPromotion(customer.getUserEmail(), subject, message, regSource);
	        }

	        // Send SMS
	        if (customer.getUserMobile() != null && !customer.getUserMobile().isBlank()) {
	            smsService.sendSMSPromotion(customer.getUserMobile(), message, regSource);
	        }
	    }
	}

	// --- Helper method to avoid null issues in .replace() ---
	private String safeValue(String value) {
	    return value != null ? value : "";
	}



}
