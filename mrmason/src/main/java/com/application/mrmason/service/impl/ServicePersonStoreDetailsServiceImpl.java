package com.application.mrmason.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.application.mrmason.dto.ResponseDeleteSPStoreDto;
import com.application.mrmason.dto.ResponseSPStoreDto;
import com.application.mrmason.dto.ServicePersonStoreResponse;
import com.application.mrmason.entity.AdminMembershipPlanEntity;
import com.application.mrmason.entity.AdminStoreVerificationEntity;
import com.application.mrmason.entity.ServicePersonStoreDetailsEntity;
import com.application.mrmason.entity.User;
import com.application.mrmason.repository.AdminMembershipPlanRepository;
import com.application.mrmason.repository.AdminStoreVerificationRepository;
import com.application.mrmason.repository.ServicePersonStoreDetailsRepo;
import com.application.mrmason.repository.UserDAO;
import com.application.mrmason.service.ServicePersonStoreDetailsService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ServicePersonStoreDetailsServiceImpl implements ServicePersonStoreDetailsService {

    private static final Logger log = LoggerFactory.getLogger(ServicePersonStoreDetailsServiceImpl.class);
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AdminMembershipPlanRepository adminMembershipPlanRepo;

    @Autowired
    private AdminStoreVerificationRepository adminStoreVerificationRepository;

    @Autowired
    private ServicePersonStoreDetailsRepo spStoreDetailsRepo;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EmailServiceImpl emailService;

    @Override
    public List<ServicePersonStoreDetailsEntity> addStores(List<ServicePersonStoreDetailsEntity> stores) {
        List<ServicePersonStoreDetailsEntity> savedStores = new ArrayList<>();

        for (ServicePersonStoreDetailsEntity store : stores) {
            try {
                log.info("Attempting to add a new store with spUserIdStoreId: {}", store.getBodSeqNoStoreId());

                // Check if the store already exists
                Optional<ServicePersonStoreDetailsEntity> spStoreExists = spStoreDetailsRepo
                        .findByBodSeqNoStoreId(store.getBodSeqNoStoreId());

                if (spStoreExists.isPresent()) {
                    log.warn("Store with spUserIdStoreId: {} already exists. Skipping save.",
                            store.getBodSeqNoStoreId());
                    continue; // Skip this store and move to the next
                }

                // Save the new store
                ServicePersonStoreDetailsEntity savedStore = spStoreDetailsRepo.save(store);
                log.info("Store added successfully with spUserIdStoreId: {}", store.getBodSeqNoStoreId());
                savedStores.add(savedStore);

                // Fetch the user associated with the store
                User user = userDAO.findByBodSeqNo(store.getBodSeqNo());
                if (user == null) {
                    throw new EntityNotFoundException("User not found for the given Service person");
                }

                // Get the user's email
                String email = user.getEmail();
                if (email != null && !email.isEmpty()) {
                    // Prepare the email content
                    String subject = "Store Added: Your New Store Details";
                    String body = String.format("<html><body>" +
                            "<h3>Hello %s,</h3>" +
                            "<p>Your new store has been successfully added! Here are the details:</p>" +
                            "<ul>" +
                            "<li><b>Store ID:</b> %s</li>" +
                            "<li><b>Location:</b> %s</li>" +
                            "</ul>" +
                            "<p>Visit <a href='https://www.mekanik.in'>www.mekanik.in</a> for more details or to manage your store.</p>"
                            +
                            "<p>Best regards,<br/>The Mekanik Team</p>" +
                            "</body></html>",
                            user.getName(), store.getBodSeqNoStoreId(), store.getLocation());

                    // Send the email to the user's email address
                    emailService.sendEmail(email, subject, body);
                    log.info("Email sent to user: {}", email);
                }

            } catch (Exception e) {
                log.error("An error occurred while adding store with spUserIdStoreId {}: {}",
                        store.getBodSeqNoStoreId(), e.getMessage(), e);
                // Handle exception if needed, such as logging failed store additions or adding
                // custom responses
            }
        }
        return savedStores;
    }

    @Override
    @Transactional
    public ResponseSPStoreDto verifyStore(ServicePersonStoreDetailsEntity store) {
        ResponseSPStoreDto response = new ResponseSPStoreDto();

        Optional<ServicePersonStoreDetailsEntity> storeOptional = spStoreDetailsRepo
                .findByBodSeqNoStoreId(store.getBodSeqNoStoreId());

        if (storeOptional.isPresent()) {
            ServicePersonStoreDetailsEntity storeOpt = storeOptional.get();

            // Fetch the verification status and default plan from
            // AdminStoreVerificationRepository
            Optional<AdminStoreVerificationEntity> storeVerificationOpt = adminStoreVerificationRepository
                    .findByBodSeqNoStoreId(store.getBodSeqNoStoreId());

            if (storeVerificationOpt.isPresent()) {
                AdminStoreVerificationEntity storeVerification = storeVerificationOpt.get();

                if ("Verified".equalsIgnoreCase(storeVerification.getVerificationStatus())) {
                    if (storeVerification.getDefaultPlan() != null && !storeVerification.getDefaultPlan().isEmpty()) {
                        // If defaultPlan is not null or empty, fetch membership plan details
                        Optional<AdminMembershipPlanEntity> membershipPlanOpt = adminMembershipPlanRepo
                                .findByDefaultPlan(storeVerification.getDefaultPlan());
                
                        if (membershipPlanOpt.isPresent()) {
                            AdminMembershipPlanEntity membershipPlan = membershipPlanOpt.get();
                
                            // Set the storeCurrentPlan and calculate the storeExpiryDate based on noOfDaysValid
                            storeOpt.setStoreCurrentPlan(membershipPlan.getMembershipPlanId());
                
                            int noOfDaysValid = Integer.parseInt(membershipPlan.getNoOfDaysValid());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.DAY_OF_YEAR, noOfDaysValid);
                
                            storeOpt.setStoreExpiryDate(calendar.getTime());
                            log.info("Store expiry date set to: {}", storeOpt.getStoreExpiryDate());
                        } else {
                            response.setMessage("Membership plan not found for defaultPlan: " + storeVerification.getDefaultPlan());
                            response.setStatus(false);
                            response.setData(storeOpt);
                            log.warn("Membership plan not found for defaultPlan: {}", storeVerification.getDefaultPlan());
                            return response;
                        }
                    } else {
                        // If defaultPlan is null or empty, reset storeCurrentPlan and storeExpiryDate
                        storeOpt.setStoreCurrentPlan(null);
                        storeOpt.setStoreExpiryDate(null);
                        log.warn("Admin verified but defaultPlan is null or empty. Not updating storeCurrentPlan and storeExpiryDate.");
                        
                        response.setMessage("Admin verified but defaultPlan is null or empty. Not updating storeCurrentPlan and storeExpiryDate.");
                        response.setStatus(true);
                        response.setData(storeOpt); // Return the updated store entity
                    }
                
                    // Update the store's verification status
                    storeOpt.setVerificationStatus("Verified");
                
                    // Set the response message and status
                    response.setMessage("Store verified and updated successfully.");
                    response.setStatus(true);
                    response.setData(storeOpt);
                
                    log.info("Store updated with verification status, current plan, and expiry date.");
                } else {
                    // If verification status is not 'Verified', reset fields
                    storeOpt.setStoreCurrentPlan(null);
                    storeOpt.setStoreExpiryDate(null);
                    storeOpt.setVerificationStatus(storeVerification.getVerificationStatus());
                
                    response.setMessage("Verification status is " + storeVerification.getVerificationStatus() + ". Verification in progress.");
                    response.setStatus(false);
                    response.setData(storeOpt);
                
                    log.info("Store verification status is not 'Verified'; resetting storeCurrentPlan and storeExpiryDate.");
                }
                
                // Save the updated store entity
                spStoreDetailsRepo.save(storeOpt);
            } else {
                response.setMessage(
                        "Verification details not found for bodSeqNoStoreId: " + store.getBodSeqNoStoreId());
                response.setStatus(false);
                log.warn("Verification details not found for bodSeqNoStoreId: {}", store.getBodSeqNoStoreId());
            }
        } else {
            response.setMessage("Store not found for bodSeqNoStoreId: " + store.getBodSeqNoStoreId());
            response.setStatus(false);
            log.warn("Store not found for bodSeqNoStoreId: {}", store.getBodSeqNoStoreId());
        }

        return response;
    }

    @Override
    public ServicePersonStoreDetailsEntity updateStore(ServicePersonStoreDetailsEntity store) {
        log.info("Attempting to update store with spUserIdStoreId: {}", store.getBodSeqNoStoreId());
        Optional<ServicePersonStoreDetailsEntity> idExists = spStoreDetailsRepo
                .findByBodSeqNoStoreId(store.getBodSeqNoStoreId());

        if (idExists.isPresent()) {
            ServicePersonStoreDetailsEntity db = idExists.get();
            db.setGst(store.getGst());
            db.setGstDocument(store.getGstDocument());
            db.setLocation(store.getLocation());
            db.setTradeLicense(store.getTradeLicense());
            db.setUpdatedBy(store.getUpdatedBy());
            log.info("Store updated successfully with spUserIdStoreId: {}", store.getBodSeqNoStoreId());
            return spStoreDetailsRepo.save(db);
        }
        log.warn("Store with spUserIdStoreId: {} not found. Update failed.", store.getBodSeqNoStoreId());
        return null;
    }

    @Override
    public List<ServicePersonStoreDetailsEntity> getSPStoreDetails(String spUserId, String storeId,
            String spUserIdStoreId, Date storeExpiryDate, String storeCurrentPlan, String verificationStatus,
            String location,
            String gst, String tradeLicense,
            String updatedBy) {
        log.info(
                "Fetching SP Store Details with spUserId: {}, storeId: {}, spUserIdStoreId: {}, location: {}, gst: {}, tradeLicense: {}, updatedBy: {}",
                spUserId, storeId, spUserIdStoreId, location, gst, tradeLicense, updatedBy, storeExpiryDate,
                storeCurrentPlan, verificationStatus);

        List<ServicePersonStoreDetailsEntity> details = spStoreDetailsRepo.findByDynamicQuery(spUserId, storeId,
                spUserIdStoreId, storeExpiryDate,
                storeCurrentPlan, verificationStatus, location, gst, tradeLicense, updatedBy);
        log.info("Retrieved {} SP Store Details", details.size());
        return details;
    }

    @Override
    public ResponseDeleteSPStoreDto deleteSPStoreDetailsById(String bodSeqNoStoreId) {
        log.info("Attempting to delete store with spUserIdStoreId: {}", bodSeqNoStoreId);

        Optional<ServicePersonStoreDetailsEntity> storeDetailsOptional = spStoreDetailsRepo
                .findByBodSeqNoStoreId(bodSeqNoStoreId);
        if (storeDetailsOptional.isPresent()) {
            ServicePersonStoreDetailsEntity storeDetails = storeDetailsOptional.get();
            spStoreDetailsRepo.deleteById(bodSeqNoStoreId);
            log.info("Store deleted successfully with bodSeqNoStoreId: {}", bodSeqNoStoreId);

            return new ResponseDeleteSPStoreDto("Data deleted successfully for spUserIdStoreId", true,
                    List.of(storeDetails));
        } else {
            log.warn("Store with spUserIdStoreId: {} not found", bodSeqNoStoreId);
            return new ResponseDeleteSPStoreDto("No data found for the given spUserIdStoreId", false, List.of());
        }
    }

    @Override
    public Optional<ServicePersonStoreDetailsEntity> findStoreById(String bodSeqNoStoreId) {
        return spStoreDetailsRepo.findByBodSeqNoStoreId(bodSeqNoStoreId);
    }

    @Override
    public Optional<ServicePersonStoreDetailsEntity> findStoreByStoreId(String storeId) {
        return spStoreDetailsRepo.findStoreByStoreId(storeId);
    }

    @Override
    public List<ServicePersonStoreResponse> getDataBy(String location) {
        log.info("Fetching SP Store Details by location: {}", location);
        List<ServicePersonStoreDetailsEntity> spStoreDetails = location != null && !location.isEmpty()
                ? spStoreDetailsRepo.findByLocation(location)
                : List.of();

        List<ServicePersonStoreResponse> response = spStoreDetails.stream()
                .map(spStore -> modelMapper.map(spStore, ServicePersonStoreResponse.class))
                .collect(Collectors.toList());
        log.info("Retrieved {} SP Store Details for location: {}", response.size(), location);
        return response;
    }
}
