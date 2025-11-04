package com.application.mrmason.service.impl;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.application.mrmason.config.AWSConfig;
import com.application.mrmason.dto.DeleteAccountRequest;
import com.application.mrmason.dto.LoginRequest;
import com.application.mrmason.dto.ResponseMessageDto;
import com.application.mrmason.dto.ResponseModel;
import com.application.mrmason.dto.ResponseSpLoginDto;
import com.application.mrmason.dto.UpdateProfileRequest;
import com.application.mrmason.dto.UserResponseDTO;
import com.application.mrmason.dto.Userdto;
import com.application.mrmason.entity.AdminSpVerification;
import com.application.mrmason.entity.DeleteUser;
import com.application.mrmason.entity.ServicePersonLogin;
import com.application.mrmason.entity.SpServiceDetails;
import com.application.mrmason.entity.UploadUserProfileImage;
import com.application.mrmason.entity.User;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.AdminSpVerificationRepository;
import com.application.mrmason.repository.DeleteUserRepo;
import com.application.mrmason.repository.SPAvailabilityRepo;
import com.application.mrmason.repository.ServicePersonLoginDAO;
import com.application.mrmason.repository.SpServiceDetailsRepo;
import com.application.mrmason.repository.UploadUserProfilemageRepository;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.security.AuthDetailsProvider;
import com.application.mrmason.security.JwtService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class UserService {
	
	@Autowired
	private AdminSpVerificationRepository adminSpVerificationRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	OtpGenerationServiceImpl otpService;

	@Autowired
	ServicePersonLoginDAO emailLoginRepo;

	@Autowired
	private AWSConfig awsConfig;

	@Autowired
	private UploadUserProfilemageRepository userProfilemageRepository;

	@Autowired
	UserDAO userDAO;

	@Autowired
	JwtService jwtService;

	@Autowired
	ServicePersonLoginDAO serviceLoginRepo;

	@Autowired
	BCryptPasswordEncoder byCrypt;

	@Autowired
	private SpServiceDetailsRepo detailsRepo;

	@Autowired
	private JavaMailSender mailsender;

	@Autowired
	private SmsService smsService;

	@Autowired
	private EmailServiceImpl emailService;

	@Autowired
	private DeleteUserRepo deleteUserRepo;
	
	@Autowired
	SPAvailabilityRepo availabilityReo;

	public Optional<User> checkExistingUser(String email, String phone, RegSource regSource) {
		List<User> users = userDAO.findByEmailANDMobile(email, phone);
		return users.stream().filter(user -> user.getRegSource().equals(regSource)).findFirst();
	}

	public boolean isEmailExists(String email) {
		return userDAO.existsByEmail(email);
	}

	public boolean isMobileExists(String mobile) {
		return userDAO.existsByMobile(mobile);
	}

	@Transactional
	public Userdto addDetails(User user) {
		String encryptPassword = byCrypt.encode(user.getPassword());
		user.setPassword(encryptPassword);

		// Email sending
		String subject = "Verify Your Email and Mobile Number";
		String emailMessage = "Thanks for registering with us. please verify your registered email and mobile.";
		emailService.sendEmail(user.getEmail(), subject, emailMessage);
		// Mobile sms sending
//		String message = "Thanks for registering with us. please verify your registered email and mobile before login. - mekanik.in";
//		smsService.registrationSendSMSMessage(user.getMobile(), message, user.getRegSource());
		User data = userDAO.save(user);

		ServicePersonLogin service = new ServicePersonLogin();
		service.setEmail(user.getEmail());
		service.setMobile(user.getMobile());
		service.setMobVerify("no");
		service.setEVerify("no");
		service.setRegSource(user.getRegSource());
		serviceLoginRepo.save(service);
		
		AdminSpVerification verification = new AdminSpVerification();
	    verification.setBodSeqNo(data.getBodSeqNo());
	    verification.setStatus("new");
	    verification.setComment("");
	    verification.setUpdateBy(data.getBodSeqNo()); // or pass admin if available
	    verification.setUpdatedDate(new Date());
	    adminSpVerificationRepository.save(verification);

		Userdto dto = new Userdto();
		dto.setName(user.getName());
		dto.setMobile(user.getMobile());
		dto.setEmail(user.getEmail());
		dto.setAddress(user.getAddress());
		dto.setCity(user.getCity());
		dto.setDistrict(user.getDistrict());
		dto.setState(user.getState());
		dto.setLocation(user.getLocation());
		dto.setVerified(user.getVerified());
		dto.setUserType(String.valueOf(data.getUserType()));
		dto.setStatus(user.getStatus());
		dto.setBusinessName(user.getBusinessName());
		dto.setBodSeqNo(user.getBodSeqNo());
		dto.setRegisteredDate(user.getRegisteredDate());
		dto.setUpdatedDate(user.getUpdatedDate());
		dto.setServiceCategory(user.getServiceCategory());
		dto.setRegSource(user.getRegSource().toString());
		dto.setLinkedInURL(user.getLinkedInURL());
		dto.setHighestQualification(user.getHighestQualification());

		return dto;

	}

	public User updateDataWithEmail(String email) {
		Optional<User> existedByEmail = Optional.of(userDAO.findByEmail(email));
		if (existedByEmail.isPresent()) {
			existedByEmail.get().setVerified("Yes");
			existedByEmail.get().setStatus("Active");
			return userDAO.save(existedByEmail.get());
		}
		return null;
	}

	public User updateDataWithMobile(String mobile) {
		Optional<User> existedByMobile = Optional.of(userDAO.findByEmailOrMobile(mobile, mobile));
		if (existedByMobile.isPresent()) {
			existedByMobile.get().setVerified("yes");
			existedByMobile.get().setStatus("active");
			return userDAO.save(existedByMobile.get());
		}
		return null;
	}

	public User updateProfile(UpdateProfileRequest registrationDetails, String bodSeqNo) {
		Optional<User> existedByEmail = Optional.of(userDAO.findByBodSeqNo(bodSeqNo));
		if (existedByEmail.isPresent()) {

			existedByEmail.get().setName(registrationDetails.getName());
			existedByEmail.get().setLocation(registrationDetails.getLocation());
			existedByEmail.get().setState(registrationDetails.getState());
			existedByEmail.get().setDistrict(registrationDetails.getDistrict());
			existedByEmail.get().setAddress(registrationDetails.getAddress());
			existedByEmail.get().setCity(registrationDetails.getCity());
			existedByEmail.get().setLinkedInURL(registrationDetails.getLinkedInURL());
			existedByEmail.get().setHighestQualification(registrationDetails.getHighestQualification());
			
			return userDAO.save(existedByEmail.get());
		}
		return null;
	}

	private static class UserInfo {
		String userId;
		String role;

		UserInfo(String userId, String role) {
			this.userId = userId;
			this.role = role;
		}
	}

	private UserInfo getLoggedInSPInfo(RegSource regSource) {
		String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();
		List<String> roleNames = loggedInRole.stream().map(GrantedAuthority::getAuthority)
				.map(role -> role.replace("ROLE_", "")).collect(Collectors.toList());
		String userId = null;
		String role = roleNames.get(0);
		UserType userType = UserType.valueOf(role);
		if (userType == UserType.Developer) {
			User user = userDAO.findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
					.orElseThrow(() -> new ResourceNotFoundException("User not found: " + loggedInUserEmail));
			userId = user.getBodSeqNo();
		}
		return new UserInfo(userId, role);
	}

	@Transactional
	public ResponseEntity<ResponseModel> uploadprofileimage(String bodSeqNo, MultipartFile photo, RegSource regSource)
			throws AccessDeniedException {

		UserInfo userInfo = getLoggedInSPInfo(regSource);
		if (!UserType.Developer.name().equals(userInfo.role)) {
			throw new AccessDeniedException("Only Developer users can access this API.");
		}

		ResponseModel response = new ResponseModel();

		// 1. Check if skuId exists in AdminMaterialMaster
		Optional<User> adminMaterial = userDAO.findByBodSeqNoUploadImage(bodSeqNo);
		if (adminMaterial.isEmpty()) {
			response.setError("true");
			response.setMsg("bodSeqNo ID not found in AdminMaterialMaster.");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		// 2. Directory for S3
		String directoryPath = "uploadphoto/" + bodSeqNo + "/";

		// 3. Prepare new UploadMatericalMasterImages entity
		UploadUserProfileImage uploadEntity = new UploadUserProfileImage();
		uploadEntity.setBodSeqNo(bodSeqNo);
		uploadEntity.setUpdatedBy(userInfo.userId);
		uploadEntity.setUpdatedDate(new Date());

		if (photo != null && !photo.isEmpty()) {
			String path1 = directoryPath + photo.getOriginalFilename();
			String link1 = awsConfig.uploadFileToS3Bucket(path1, photo);
			uploadEntity.setPhoto(link1);
		}
		userProfilemageRepository.save(uploadEntity);
		response.setError("false");
		response.setMsg("Profile photo uploaded successfully.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public String changePassword(String email, String oldPassword, String newPassword, String confirmPassword,
			RegSource regSource) {
		Optional<User> user = userDAO.findByEmailAndRegSource(email, regSource);
		if (user.isPresent()) {
			if (byCrypt.matches(oldPassword, user.get().getPassword())) {
				if (newPassword.equals(confirmPassword)) {
					String encryptPassword = byCrypt.encode(confirmPassword);
					user.get().setPassword(encryptPassword);
					userDAO.save(user.get());
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

	public String sendMail(String email, RegSource regSource) {
		Optional<User> userOp = userDAO.findByEmailAndRegSource(email, regSource);
		if (userOp.isPresent()) {
			User user = userOp.get();
			if ("active".equalsIgnoreCase(user.getStatus())) {
				otpService.generateOtp(email, regSource);
				return "otp";
			}
		}
		return null;
	}

	public String sendSms(String mobile, RegSource regSource) {
		Optional<User> userOp = userDAO.findByMobileAndRegSource(mobile, regSource);
		if (userOp.isPresent()) {
			User user = userOp.get();
			if ("active".equalsIgnoreCase(user.getStatus())) {
				otpService.generateMobileOtp(mobile, regSource);
				return "otp";
			}
		}
		return null;
	}

	public String forgetPassword(String mobile, String email, String otp, String newPass, String confPass,
			RegSource regSource) {
		Optional<User> userEmail = userDAO.findByEmailAndRegSource(email, regSource);
		Optional<User> userMobile = userDAO.findByMobileAndRegSource(mobile, regSource);
		if (userEmail.isPresent()) {
			if (otpService.verifyOtp(email, otp, regSource)) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					userEmail.get().setPassword(encryptPassword);
					userDAO.save(userEmail.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else if (userMobile.isPresent()) {
			if (otpService.verifyMobileOtp(mobile, otp)) {
				if (newPass.equals(confPass)) {
					String encryptPassword = byCrypt.encode(confPass);
					userMobile.get().setPassword(encryptPassword);
					userDAO.save(userMobile.get());
					return "changed";
				} else {
					return "notMatched";
				}
			} else {
				return "incorrect";
			}
		} else if (!userEmail.isPresent() && userMobile.isPresent()) {
			return "incorrectEmail";
		}
		return null;

	}

	public Userdto getServiceProfile(String bodSeqNo) {

		Optional<User> user = Optional.ofNullable(userDAO.findByBodSeqNo(bodSeqNo));
		List<SpServiceDetails> serviceDetails = detailsRepo.findByUserId(user.get().getBodSeqNo());

		if (user.isPresent()) {
			User userdb = user.get();

			Userdto dto = new Userdto();
			dto.setName(userdb.getName());
			dto.setMobile(userdb.getMobile());
			dto.setEmail(userdb.getEmail());
			dto.setAddress(userdb.getAddress());
			dto.setCity(userdb.getCity());
			dto.setDistrict(userdb.getDistrict());
			dto.setState(userdb.getState());
			dto.setLocation(userdb.getLocation());
			dto.setRegSource(userdb.getRegSource().toString());
			if (!serviceDetails.isEmpty()) {
				SpServiceDetails sd = serviceDetails.get(0);
				dto.setAvailableLocation(sd.getCity());
			}

//			dto.setPincodeNo(userdb.getPincodeNo());

			dto.setVerified(userdb.getVerified());
			dto.setUserType(String.valueOf(userdb.getUserType()));
			dto.setStatus(userdb.getStatus());
			dto.setBusinessName(userdb.getBusinessName());
			dto.setBodSeqNo(userdb.getBodSeqNo());
			dto.setRegisteredDate(userdb.getRegisteredDate());
			dto.setUpdatedDate(userdb.getUpdatedDate());
			dto.setServiceCategory(userdb.getServiceCategory());
			userProfilemageRepository.findByBodSeqNo(bodSeqNo).ifPresent(upload -> dto.setPhoto(upload.getPhoto()));
			
			adminSpVerificationRepository.findByBodSeqNo(bodSeqNo).ifPresent(status -> dto.setVerifiedStatus(status.getStatus()));
			
			dto.setLinkedInURL(userdb.getLinkedInURL());
			dto.setHighestQualification(userdb.getHighestQualification());
			return dto;
		}

		return null;

	}

	public Userdto getServiceProfile(String email, RegSource regSource) {

		Optional<User> user = userDAO.findByEmailAndRegSource(email, regSource);
		List<SpServiceDetails> serviceDetails = detailsRepo.findByUserId(user.get().getBodSeqNo());

		if (user.isPresent()) {
			User userdb = user.get();

			Userdto dto = new Userdto();
			dto.setName(userdb.getName());
			dto.setMobile(userdb.getMobile());
			dto.setEmail(userdb.getEmail());
			availabilityReo.findByBodSeqNos(user.get().getBodSeqNo())
            .ifPresent(spa -> dto.setCurrentLocation(spa.getAddress()));
			
			dto.setAddress(userdb.getAddress());
			dto.setCity(userdb.getCity());
			dto.setDistrict(userdb.getDistrict());
			dto.setState(userdb.getState());
			dto.setLinkedInURL(userdb.getLinkedInURL());
			dto.setHighestQualification(userdb.getHighestQualification());
			dto.setLocation(userdb.getLocation());
			dto.setRegSource(userdb.getRegSource().toString());
			if (!serviceDetails.isEmpty()) {
				SpServiceDetails sd = serviceDetails.get(0);
				dto.setAvailableLocation(sd.getCity());
			}
			List<SpServiceDetails> serviceDetail= detailsRepo.findByUserIdAndStatus(user.get().getBodSeqNo(), "active");
		    List<String> serviceTypes = serviceDetail.stream()
		        .map(SpServiceDetails::getServiceType)
		        .toList();
		    
		    dto.setServiceType(serviceTypes);
		    userProfilemageRepository.findByBodSeqNo(user.get().getBodSeqNo()).ifPresent(upload -> dto.setPhoto(upload.getPhoto()));
			
			adminSpVerificationRepository.findByBodSeqNo(user.get().getBodSeqNo()).ifPresent(status -> dto.setVerifiedStatus(status.getStatus()));
//			dto.setPincodeNo(userdb.getPincodeNo());
			dto.setVerified(userdb.getVerified());
			dto.setUserType(String.valueOf(userdb.getUserType()));
			dto.setStatus(userdb.getStatus());
			dto.setBusinessName(userdb.getBusinessName());
			dto.setBodSeqNo(userdb.getBodSeqNo());
			dto.setRegisteredDate(userdb.getRegisteredDate());
			dto.setUpdatedDate(userdb.getUpdatedDate());
			dto.setServiceCategory(userdb.getServiceCategory());
			return dto;
		}

		return null;

	}

//	public List<User> getServicePersonData(String email, String mobile, String location, String status, String category,
//			String fromDate, String toDate) {
//		if (fromDate == null && toDate == null && location == null && category == null && status != null
//				|| email != null || mobile != null) {
//			return userDAO.findByEmailOrMobileOrStatusOrderByRegisteredDateDesc(email, mobile, status);
//		} else if (category != null) {
//			return userDAO.findByServiceCategory(category);
//		} else if (location != null) {
//			return userDAO.findByLocation(location);
//		} else {
//			return userDAO.findByRegisteredDateBetween(fromDate, toDate);
//		}
//
//	}
	    public List<UserResponseDTO> getServicePersonData(
	            String email, String mobile, String location, String status,
	            String category, String fromDate, String toDate,
	            String state, String city,String serviceType,
	            Map<String, String> requestParams) {

	        List<String> expectedParams = Arrays.asList(
	                "email", "mobile", "location", "status", "serviceCategory",
	                "fromDate", "toDate", "state", "city" , "serviceSubCategory"
	        );

	        for (String paramName : requestParams.keySet()) {
	            if (!expectedParams.contains(paramName)) {
	                throw new IllegalArgumentException("Unexpected parameter '" + paramName + "' is not allowed.");
	            }
	        }

	        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<User> query = cb.createQuery(User.class);
	        Root<User> root = query.from(User.class);

	        List<Predicate> predicates = new ArrayList<>();

	        if (email != null) {
	            predicates.add(cb.equal(root.get("email"), email));
	        }
	        if (mobile != null) {
	            predicates.add(cb.equal(root.get("mobile"), mobile));
	        }
	        if (location != null) {
	            predicates.add(cb.equal(root.get("location"), location));
	        }
	        if (status != null) {
	            predicates.add(cb.equal(root.get("status"), status));
	        }
	        if (category != null) {
	            predicates.add(cb.equal(root.get("serviceCategory"), category));
	        }
	        if (state != null) {
	            predicates.add(cb.equal(root.get("state"), state));
	        }
	        if (city != null) {
	            predicates.add(cb.equal(root.get("city"), city));
	        }
	        if (fromDate != null && toDate != null) {
				predicates.add(cb.between(root.get("registeredDate"), fromDate, toDate));
			} else if (fromDate != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("registeredDate"), fromDate));
			} else if (toDate != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("registeredDate"), toDate));
			}
	        
	        query.where(predicates.toArray(new Predicate[0]));

	        List<User> users = entityManager.createQuery(query).getResultList();
	     // 🔍 Apply additional serviceType filter if present
	     if (serviceType != null && !serviceType.isEmpty()) {
	         users = users.stream()
	             .filter(user -> {
	                 List<SpServiceDetails> serviceDetails = detailsRepo.findByUserId(user.getBodSeqNo());
	                 return serviceDetails.stream()
	                         .anyMatch(s -> serviceType.equalsIgnoreCase(s.getServiceType()));
	             })
	             .collect(Collectors.toList());
	     }
	        return users.stream().map(this::convertToDto).collect(Collectors.toList());
	    }

	    private UserResponseDTO convertToDto(User user) {
	        UserResponseDTO dto = new UserResponseDTO();
	        dto.setBodSeqNo(user.getBodSeqNo());
	        dto.setName(user.getName());
	        dto.setBusinessName(user.getBusinessName());
	        dto.setMobile(user.getMobile());
	        dto.setEmail(user.getEmail());
	        dto.setAddress(user.getAddress());
	        dto.setCity(user.getCity());
	        dto.setDistrict(user.getDistrict());
	        dto.setState(user.getState());
	        dto.setLocation(user.getLocation());
	        dto.setRegisteredDate(user.getRegisteredDate());
	        dto.setVerified(user.getVerified());
	        dto.setServiceCategory(user.getServiceCategory());
	        dto.setStatus(user.getStatus());
	        dto.setRegSource(user.getRegSource() != null ? user.getRegSource().name() : null);
	        dto.setEnabled(user.isEnabled());
	        dto.setAccountNonExpired(user.isAccountNonExpired());
	        dto.setAccountNonLocked(user.isAccountNonLocked());
	        dto.setCredentialsNonExpired(user.isCredentialsNonExpired());
	        dto.setUsername(user.getUsername());

	        // Set authority strings directly
	        dto.setAuthorities(
	                user.getAuthorities().stream()
	                        .map(GrantedAuthority::getAuthority)
	                        .collect(Collectors.toList())
	        );

	        	List<SpServiceDetails> serviceDetailsList = detailsRepo.findByUserId(user.getBodSeqNo());
	            List<String> serviceTypes = serviceDetailsList.stream()
	                .map(SpServiceDetails::getServiceType)
	                .collect(Collectors.toList());
	            dto.setServiceType(serviceTypes);
	        return dto;
	    
	}


	public ResponseSpLoginDto loginDetails(LoginRequest login) {

		Optional<ServicePersonLogin> loginDb = emailLoginRepo.findByEmailOrMobileAndRegSource(login.getEmail(),
				login.getMobile(), login.getRegSource());
		ResponseSpLoginDto response = new ResponseSpLoginDto();

		if (loginDb.isPresent()) {
			Optional<User> userEmailMobile = userDAO.findByEmailOrMobileAndRegSource(login.getEmail(),
					login.getMobile(), login.getRegSource());
			User user = userEmailMobile.get();
			String status = user.getStatus();

			if (userEmailMobile.isPresent()) {
				if (status != null && status.equalsIgnoreCase("active")) {
					if (login.getEmail() != null && login.getMobile() == null) {
						if (loginDb.get().getEVerify().equalsIgnoreCase("yes")) {

							if (byCrypt.matches(login.getPassword(), user.getPassword())) {
								String jwtToken = jwtService.generateToken(userEmailMobile.get(), user.getBodSeqNo());
								response.setJwtToken(jwtToken);
								response.setMessage("Login Successful.");
								response.setStatus(true);
								response.setLoginDetails(getServiceProfile(login.getEmail(), login.getRegSource()));
								return response;
							} else {
								response.setMessage("Invalid Password");
								response.setStatus(false);
								return response;
							}
						} else {
							response.setMessage("verify Email");
							response.setStatus(false);
							return response;
						}
					} else if (login.getEmail() == null && login.getMobile() != null) {
						if (loginDb.get().getMobVerify().equalsIgnoreCase("yes")) {

							if (byCrypt.matches(login.getPassword(), user.getPassword())) {
								String jwtToken = jwtService.generateToken(userEmailMobile.get(), user.getBodSeqNo());
								response.setJwtToken(jwtToken);
								response.setMessage("Login Successful.");
								response.setStatus(true);
								response.setLoginDetails(
										getServiceProfile(loginDb.get().getEmail(), login.getRegSource()));
								return response;
							} else {
								response.setMessage("Invalid Password");
								response.setStatus(false);
								return response;
							}
						} else {
							response.setMessage("verify Mobile");
							response.setStatus(false);
							return response;
						}
					}
				} else {
					response.setMessage("Account status : " + user.getStatus());
					response.setStatus(false);
					return response;
				}
			} else {
				response.setMessage("Inactive User");
				response.setStatus(false);
				return response;
			}

		}
		response.setMessage("Invalid User.!");
		response.setStatus(false);
		return response;
	}

	public User getServiceDataProfile(String email) {

		Optional<User> user = Optional.ofNullable(userDAO.findByEmail(email));
		return user.get();
	}

	@Transactional
	public ResponseMessageDto servicePersonDeleteAccount(DeleteAccountRequest accountRequest) {
		ResponseMessageDto response = new ResponseMessageDto();

		// Retrieve the user based on the given ID
		User user = userDAO.findByBodSeqNo(accountRequest.getSpId());
		if (user == null) {
			response.setMessage("Account not found.");
			response.setStatus(false);
			return response;
		}

		// Retrieve service person login details by email or mobile and registration
		// source
		ServicePersonLogin servicePersonLogin = emailLoginRepo.findByEmailOrMobileAndRegSource(user.getEmail(),
				user.getRegSource());

		// Create and save a record in DeleteUser for tracking the deletion reason and
		// date
		DeleteUser deleteUser = new DeleteUser();
		deleteUser.setDeletedDate(LocalDateTime.now());
		deleteUser.setDeleteReason(accountRequest.getReason());
		deleteUser.setEmail(user.getEmail());
		deleteUser.setPhone(user.getMobile());
		deleteUser.setDeactivated(true);
		deleteUser.setCandidateId(user.getBodSeqNo());
		deleteUserRepo.save(deleteUser);
		emailLoginRepo.delete(servicePersonLogin);
		// Delete user and service person login
		userDAO.delete(user);
//	    servicePersonLogin.ifPresent(emailLoginRepo::delete);

		// Set response message for successful account deletion
		response.setMessage("Account deleted. Thank you for being with us.");
		response.setStatus(true);
		return response;
	}

//	public void sendEmail(String toMail) {
//		SimpleMailMessage mail = new SimpleMailMessage();
//		mail.setTo(toMail);
//		mail.setSubject("Verify Your Email and Mobile Number");
//		String body = "Thanks for registering with us. please verify your registered email and mobile.";
//		mail.setText(body);
//		mailsender.send(mail);
//	}
}
