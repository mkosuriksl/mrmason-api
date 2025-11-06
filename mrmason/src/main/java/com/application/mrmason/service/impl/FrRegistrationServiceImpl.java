package com.application.mrmason.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.FrLoginRequest;
import com.application.mrmason.dto.FrRegRequestDto;
import com.application.mrmason.dto.FrRegResponseDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.OtpDto;
import com.application.mrmason.dto.ResetChangePassOtpDto;
import com.application.mrmason.dto.ResponseFrLoginDto;
import com.application.mrmason.dto.UserFrDto;
import com.application.mrmason.entity.FrLogin;
import com.application.mrmason.entity.FrProfile;
import com.application.mrmason.entity.FrReg;
import com.application.mrmason.entity.FrUserOtp;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.repository.FrLoginRepo;
import com.application.mrmason.repository.FrProfileRepository;
import com.application.mrmason.repository.FrRegRepository;
import com.application.mrmason.repository.FrUserOtpRepository;
import com.application.mrmason.security.JwtService;
import com.application.mrmason.service.EmailService;
import com.application.mrmason.service.FrRegistrationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrRegistrationServiceImpl implements FrRegistrationService {

	private final FrRegRepository frRegRepo;
	private final FrUserOtpRepository otpRepo;
	private final EmailService emailService;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@Autowired
	private FrLoginRepo frLoginRepo;
	@Autowired
	SmsService smsService;
	@Autowired
	JwtService jwtService;
	@Autowired
	FrProfileRepository frProfileRepository;
	@Autowired
	BCryptPasswordEncoder byCrypt;

	private String now() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
	}

	@Override
	public GenericResponse<FrRegResponseDto> registerUser(FrRegRequestDto dto) {
		Optional<FrReg> existingEmail = frRegRepo.findByFrEmail(dto.getFrEmail());
		Optional<FrReg> existingMobile = frRegRepo.findByFrMobile(dto.getFrMobile());

		if (existingEmail.isPresent() || existingMobile.isPresent()) {
			return new GenericResponse<>("User already registered", false, null);
		}

		String userId = "FR" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

		// ✅ Save user registration
		FrReg reg = FrReg.builder().frUserId(userId).frEmail(dto.getFrEmail()).frMobile(dto.getFrMobile())
				.password(passwordEncoder.encode(dto.getFrPassword())).frLinkedInProfile(dto.getFrLinkedInProfile())
				.emailVerified("no").mobileVerified("no").regSource(dto.getRegSource()).userType(dto.getUserType())
				.status("INACTIVE").updatedDate(LocalDateTime.now()).build();

		frRegRepo.save(reg);

		// ✅ Store OTP details in FrUserOtp
		FrUserOtp frUserOtp = FrUserOtp.builder().frUserid(userId).frEmail(reg.getFrEmail()).frMobile(reg.getFrMobile())
				.regSource(reg.getRegSource()).userType(reg.getUserType()).updatedDate(LocalDateTime.now()).build();

		otpRepo.save(frUserOtp);

		// ✅ Send OTP to user's email
		emailService.sendEmail(reg.getFrEmail(), reg.getRegSource());

		// ✅ Prepare response DTO
		FrRegResponseDto responseDto = FrRegResponseDto.builder().frUserid(reg.getFrUserId()).frEmail(reg.getFrEmail())
				.frMobile(reg.getFrMobile()).frLinkedInProfile(reg.getFrLinkedInProfile())
				.emailVerified(reg.getEmailVerified()).mobileVerified(reg.getMobileVerified())
				.regSource(reg.getRegSource()).userType(reg.getUserType()).status(reg.getStatus())
				.updatedDate(reg.getUpdatedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"))).build();

		return new GenericResponse<>(
				"Thanks for registering with us. Please verify your registered email. OTP has been sent.", true,
				responseDto);
	}

	@Override
	public GenericResponse<Void> sendOtp(OtpDto dto) {
		int otp = (int) (Math.random() * 900000) + 100000;
		String otpStr = String.valueOf(otp);

		FrUserOtp frUserOtp;

		// ✅ Case 1: Email
		if (dto.getEmailOrMobile().contains("@")) {
			Optional<FrUserOtp> existing = otpRepo.findByFrEmailAndRegSource(dto.getEmailOrMobile(),
					dto.getRegSource());
			if (existing.isPresent()) {
				frUserOtp = existing.get();
				frUserOtp.setFrEmailOtp(otpStr);
			} else {
				frUserOtp = FrUserOtp.builder().frEmail(dto.getEmailOrMobile()).frEmailOtp(otpStr)
						.regSource(dto.getRegSource()).updatedDate(LocalDateTime.now()).build();
			}
		}
		// ✅ Case 2: Mobile
		else {
			Optional<FrUserOtp> existing = otpRepo.findByFrMobileAndRegSource(dto.getEmailOrMobile(),
					dto.getRegSource());
			if (existing.isPresent()) {
				frUserOtp = existing.get();
				frUserOtp.setFrMobileOtp(otpStr);
			} else {
				frUserOtp = FrUserOtp.builder().frMobile(dto.getEmailOrMobile()).frMobileOtp(otpStr)
						.regSource(dto.getRegSource()).updatedDate(LocalDateTime.now()).build();
			}
		}

		frUserOtp.setUpdatedDate(LocalDateTime.now());
		otpRepo.save(frUserOtp);

		// ✅ Send OTP
		boolean sent = false;
		if (dto.getEmailOrMobile().contains("@")) {
			emailService.sendEmail(dto.getEmailOrMobile(), otpStr, dto.getRegSource());
			sent = true;
		} else {
			boolean smsSent = smsService.sendSMSMessage(dto.getEmailOrMobile(), otpStr, dto.getRegSource());
			if (smsSent)
				sent = true;
		}

		return sent ? new GenericResponse<>("OTP sent successfully", true, null)
				: new GenericResponse<>("Failed to send OTP. Please try again.", false, null);
	}

	@Override
	public GenericResponse<String> verifyOtp(OtpDto dto) {
		Optional<FrUserOtp> otpRecord;

		// Determine if input is email or mobile
		if (dto.getEmailOrMobile().contains("@")) {
			otpRecord = otpRepo.findByFrEmailAndRegSource(dto.getEmailOrMobile(), dto.getRegSource());
		} else {
			otpRecord = otpRepo.findByFrMobileAndRegSource(dto.getEmailOrMobile(), dto.getRegSource());
		}

		if (otpRecord.isPresent()) {
			FrUserOtp otp = otpRecord.get();

			// Compare OTP
			if (dto.getOtp().equals(otp.getFrEmailOtp()) || dto.getOtp().equals(otp.getFrMobileOtp())) {

				// ✅ Update verification flags
				if (dto.getEmailOrMobile().contains("@")) {
					otp.setEmailVerified("yes");
				} else {
					otp.setMobileVerified("yes");
				}
				otp.setUpdatedDate(LocalDateTime.now());
				otpRepo.save(otp);

				// ✅ Fetch user from FrReg table
				Optional<FrReg> regUser = frRegRepo.findByFrUserId(otp.getFrUserid());
				if (regUser.isPresent()) {
					FrReg user = regUser.get();
					user.setStatus("ACTIVE");
					if (dto.getEmailOrMobile().contains("@")) {
						user.setEmailVerified("yes");
					} else {
						user.setMobileVerified("yes");
					}
					user.setUpdatedDate(LocalDateTime.now());
					frRegRepo.save(user);

					// ✅ Insert or update record in fr_login table
					Optional<FrLogin> existingLogin;
					if (dto.getEmailOrMobile().contains("@")) {
						existingLogin = frLoginRepo.findByFrEmailAndRegSource(user.getFrEmail(), user.getRegSource());
					} else {
						existingLogin = frLoginRepo.findByFrMobileAndRegSource(user.getFrMobile(), user.getRegSource());
					}

					FrLogin login = existingLogin.orElseGet(() -> FrLogin.builder().frUserid(user.getFrUserId())
							.frEmail(user.getFrEmail()).frMobile(user.getFrMobile()).regSource(user.getRegSource())
							.userType(user.getUserType()).build());

					// ✅ Update verification + status + password
					if (dto.getEmailOrMobile().contains("@")) {
						login.setEmailVerified("yes");
					} else {
						login.setMobileVerified("yes");
					}
					login.setStatus("ACTIVE");
					login.setPassword(user.getPassword());
					login.setUpdatedDate(LocalDateTime.now());

					frLoginRepo.save(login);
				}

				return new GenericResponse<>("OTP verified successfully. User activated.", true, null);
			}
		}

		return new GenericResponse<>("Invalid OTP", false, null);
	}

	

	@Override
	public GenericResponse<FrProfile> addOrUpdateProfile(FrProfile profile) {
		// Check if frUserId exists in fr_reg
		Optional<FrReg> frRegOptional = frRegRepo.findByFrUserId(profile.getFrUserId());
		if (frRegOptional.isEmpty()) {
			return new GenericResponse<>("User ID not found in registration records.", false, null);
		}

		FrReg frReg = frRegOptional.get();

		// Check verification
		if (!"yes".equalsIgnoreCase(frReg.getEmailVerified()) && !"yes".equalsIgnoreCase(frReg.getMobileVerified())) {
			return new GenericResponse<>("Email or Mobile must be verified before updating profile.", false, null);
		}

		// Update or Create profile
		Optional<FrProfile> existingProfileOpt = frProfileRepository.findByFrUserId(profile.getFrUserId());
		FrProfile savedProfile;

		if (existingProfileOpt.isPresent()) {
			FrProfile existing = existingProfileOpt.get();
			existing.setPrimarySkill(profile.getPrimarySkill());
			existing.setPrimaryYoe(profile.getPrimaryYoe());
			existing.setPrimaryRateMySelf(profile.getPrimaryRateMySelf());
			existing.setSecondarySkill(profile.getSecondarySkill());
			existing.setSecondaryYoe(profile.getSecondaryYoe());
			existing.setSecondaryRateMySelf(profile.getSecondaryRateMySelf());
			existing.setUpdatedBy(frReg.getFrUserId());
			savedProfile = frProfileRepository.save(existing);
			return new GenericResponse<>("Profile updated successfully.", true, savedProfile);
		} else {
			savedProfile = frProfileRepository.save(profile);
			return new GenericResponse<>("Profile created successfully.", true, savedProfile);
		}
	}

	@Override
	public ResponseFrLoginDto login(FrLoginRequest login) {

		Optional<FrLogin> loginDb = frLoginRepo.findByFrEmailOrFrMobileAndRegSource(login.getEmail(), login.getMobile(),
				login.getRegSource());
		ResponseFrLoginDto response = new ResponseFrLoginDto();

		if (loginDb.isPresent()) {
			Optional<FrReg> userEmailMobile = frRegRepo.findByFrEmailOrFrMobileAndRegSource(login.getEmail(),
					login.getMobile(), login.getRegSource());
			FrReg user = userEmailMobile.get();
			String status = user.getStatus();

			if (userEmailMobile.isPresent()) {
				if (status != null && status.equalsIgnoreCase("ACTIVE")) {
					if (login.getEmail() != null && login.getMobile() == null) {
						if (loginDb.get().getEmailVerified().equalsIgnoreCase("yes")) {

							if (byCrypt.matches(login.getPassword(), user.getPassword())) {
								String jwtToken = jwtService.generateToken(userEmailMobile.get(), user.getFrUserId());
								response.setJwtToken(jwtToken);
								response.setMessage("Login Successful.");
								response.setStatus(true);
								response.setLoginDetails(getProfile(login.getEmail(), login.getRegSource()));
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
						if (loginDb.get().getMobileVerified().equalsIgnoreCase("yes")) {

							if (byCrypt.matches(login.getPassword(), user.getPassword())) {
								String jwtToken = jwtService.generateToken(userEmailMobile.get(), user.getFrUserId());
								response.setJwtToken(jwtToken);
								response.setMessage("Login Successful.");
								response.setStatus(true);
								response.setLoginDetails(getProfile(loginDb.get().getFrEmail(), login.getRegSource()));
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

	public UserFrDto getProfile(String email, RegSource regSource) {
		Optional<FrReg> user = frRegRepo.findByFrEmailAndRegSource(email, regSource);
		List<FrProfile> userProfile = frProfileRepository.findAllByFrUserId(user.get().getFrUserId());

		if (user.isPresent()) {
			FrReg userdb = user.get();

			UserFrDto dto = new UserFrDto();
			dto.setFrUserId(userdb.getFrUserId());
			dto.setFrEmail(userdb.getFrEmail());
			dto.setFrMobile(userdb.getFrMobile());
			dto.setFrLinkedInProfile(userdb.getFrLinkedInProfile());
			dto.setEmailVerified(userdb.getEmailVerified());
			dto.setMobileVerified(userdb.getMobileVerified());
			dto.setRegSource(userdb.getRegSource());
			dto.setStatus(userdb.getStatus());
			dto.setUserType(userdb.getUserType());
			if (!userProfile.isEmpty()) {
				FrProfile fp = userProfile.get(0);
				dto.setPrimarySkill(fp.getPrimarySkill());
			}
			return dto;
		}

		return null;

	}

	@Override
	public GenericResponse<Void> forgotSendOtp(OtpDto dto) {
	    String emailOrMobile = dto.getEmailOrMobile();
	    RegSource regSource = dto.getRegSource();

	    // ✅ Find user by email or mobile
	    Optional<FrLogin> existingUser;
	    if (emailOrMobile.contains("@")) {
	        existingUser = frLoginRepo.findByFrEmailAndRegSource(emailOrMobile, regSource);
	    } else {
	        existingUser = frLoginRepo.findByFrMobileAndRegSource(emailOrMobile, regSource);
	    }

	    if (!existingUser.isPresent()) {
	        return new GenericResponse<>("User not found. Please check your email or mobile.", false, null);
	    }

	    // ✅ Generate OTP
	    String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

	    // ✅ Store OTP
	    FrUserOtp frUserOtp;
	    if (emailOrMobile.contains("@")) {
	        Optional<FrUserOtp> existingOtp = otpRepo.findByFrEmailAndRegSource(emailOrMobile, regSource);
	        frUserOtp = existingOtp.orElse(new FrUserOtp());
	        frUserOtp.setFrEmail(emailOrMobile);
	        frUserOtp.setFrEmailOtp(otp);
	    } else {
	        Optional<FrUserOtp> existingOtp = otpRepo.findByFrMobileAndRegSource(emailOrMobile, regSource);
	        frUserOtp = existingOtp.orElse(new FrUserOtp());
	        frUserOtp.setFrMobile(emailOrMobile);
	        frUserOtp.setFrMobileOtp(otp);
	    }
	    frUserOtp.setRegSource(regSource);
	    frUserOtp.setUpdatedDate(LocalDateTime.now());
	    otpRepo.save(frUserOtp);

	    // ✅ Send OTP (email or SMS)
	    boolean sent = false;
	    if (emailOrMobile.contains("@")) {
	        emailService.sendEmail(emailOrMobile, otp, regSource);
	        sent = true;
	    } else {
	        boolean smsSent = smsService.sendSMSMessage(emailOrMobile, otp, regSource);
	        if (smsSent) sent = true;
	    }

	    if (sent) {
	        return new GenericResponse<>("Forgot OTP sent successfully.", true, null);
	    } else {
	        return new GenericResponse<>("Failed to send OTP. Please try again.", false, null);
	    }
	}

	@Override
	public GenericResponse<Void> forgotVerifyOtp(OtpDto dto) {
	    String emailOrMobile = dto.getEmailOrMobile();
	    String otp = dto.getOtp();
	    String newPassword = dto.getNewPass();
	    String confPassword = dto.getConfPass();
	    RegSource regSource = dto.getRegSource();

	    if (!newPassword.equals(confPassword)) {
	        return new GenericResponse<>("New password and confirm password do not match.", false, null);
	    }

	    Optional<FrUserOtp> otpRecord;
	    if (emailOrMobile.contains("@")) {
	        otpRecord = otpRepo.findByFrEmailAndRegSource(emailOrMobile, regSource);
	    } else {
	        otpRecord = otpRepo.findByFrMobileAndRegSource(emailOrMobile, regSource);
	    }

	    if (otpRecord.isEmpty()) {
	        return new GenericResponse<>("OTP not found. Please request again.", false, null);
	    }

	    FrUserOtp userOtp = otpRecord.get();

	    // ✅ Verify OTP
	    if (!(otp.equals(userOtp.getFrEmailOtp()) || otp.equals(userOtp.getFrMobileOtp()))) {
	        return new GenericResponse<>("Invalid OTP.", false, null);
	    }

	    // ✅ Find user in FrLogin
	    Optional<FrLogin> userOpt;
	    if (emailOrMobile.contains("@")) {
	        userOpt = frLoginRepo.findByFrEmailAndRegSource(emailOrMobile, regSource);
	    } else {
	        userOpt = frLoginRepo.findByFrMobileAndRegSource(emailOrMobile, regSource);
	    }

	    if (!userOpt.isPresent()) {
	        return new GenericResponse<>("User not found.", false, null);
	    }

	    FrLogin user = userOpt.get();
	    String encodedPass = byCrypt.encode(newPassword);

	    // ✅ Update password in FrLogin
	    user.setPassword(encodedPass);
	    user.setUpdatedDate(LocalDateTime.now());
	    frLoginRepo.save(user);

	    // ✅ Also update in FrReg (keep both in sync)
	    Optional<FrReg> frRegOpt = frRegRepo.findByFrUserId(user.getFrUserid());
	    if (frRegOpt.isPresent()) {
	        FrReg reg = frRegOpt.get();
	        reg.setPassword(encodedPass);
	        reg.setUpdatedDate(LocalDateTime.now());
	        frRegRepo.save(reg);
	    }

	    // ✅ Mark OTP used
	    userOtp.setFrEmailOtp(null);
	    userOtp.setFrMobileOtp(null);
	    otpRepo.save(userOtp);

	    return new GenericResponse<>("OTP verified successfully. You can now reset your password.", true, null);
	}

	@Override
	public GenericResponse<Void> changePassword(ResetChangePassOtpDto dto) {
	    String emailOrMobile = dto.getEmailOrMobile();
	    String oldPassword = dto.getOldPassword();
	    String newPassword = dto.getNewPass();
	    String confirmPassword = dto.getConfPass();
	    RegSource regSource = dto.getRegSource();

	    // ✅ Check new password match
	    if (!newPassword.equals(confirmPassword)) {
	        return new GenericResponse<>("New password and confirm password do not match.", false, null);
	    }

	    // ✅ Find user by email or mobile
	    Optional<FrLogin> userOpt;
	    if (emailOrMobile.contains("@")) {
	        userOpt = frLoginRepo.findByFrEmailAndRegSource(emailOrMobile, regSource);
	    } else {
	        userOpt = frLoginRepo.findByFrMobileAndRegSource(emailOrMobile, regSource);
	    }

	    if (userOpt.isEmpty()) {
	        return new GenericResponse<>("User not found.", false, null);
	    }

	    FrLogin user = userOpt.get();

	    // ✅ Verify old password
	    if (!byCrypt.matches(oldPassword, user.getPassword())) {
	        return new GenericResponse<>("Invalid old password.", false, null);
	    }

	    // ✅ Encode and update new password
	    String encodedNewPassword = byCrypt.encode(newPassword);
	    user.setPassword(encodedNewPassword);
	    user.setUpdatedDate(LocalDateTime.now());
	    frLoginRepo.save(user);

	    // ✅ Also update password in FrReg (keep consistency)
	    Optional<FrReg> frRegOpt = frRegRepo.findByFrUserId(user.getFrUserid());
	    if (frRegOpt.isPresent()) {
	        FrReg reg = frRegOpt.get();
	        reg.setPassword(encodedNewPassword);
	        reg.setUpdatedDate(LocalDateTime.now());
	        frRegRepo.save(reg);
	    }

	    return new GenericResponse<>("Password changed successfully.", true, null);
	}

}
