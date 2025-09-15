package com.application.mrmason.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MaterialPricingRequestDto;
import com.application.mrmason.entity.MaterialPricing;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.MaterialPricingRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;
import com.application.mrmason.security.AuthDetailsProvider;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class MaterialPricingService {

    @Autowired
    private MaterialPricingRepository materialPricingRepository;
    
    @Autowired
    private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<MaterialPricing> savePricings(List<MaterialPricingRequestDto> dtos, RegSource regSource) {
    	  UserInfo userInfo = getLoggedInUserInfo(regSource);
          String userId = userInfo.userId;
        List<MaterialPricing> pricings = new ArrayList<>();

        for (MaterialPricingRequestDto dto : dtos) {
            if (materialPricingRepository.existsById(dto.getUserIdSku())) {
                throw new RuntimeException("Pricing already exists for userIdSku: " + dto.getUserIdSku());
            }

            MaterialPricing pricing = new MaterialPricing();
            pricing.setUserIdSku(dto.getUserIdSku());
            pricing.setMrp(dto.getMrp());
            pricing.setDiscount(dto.getDiscount());
            pricing.setAmount(dto.getAmount());
            pricing.setGst(dto.getGst());
            pricing.setUpdatedBy(userId);
            pricing.setUpdatedDate(LocalDateTime.now());

            pricings.add(pricing);
        }

        return materialPricingRepository.saveAll(pricings);
    }

    @Transactional
    public List<MaterialPricing> updatePricings(List<MaterialPricingRequestDto> dtos, RegSource regSource) {
        String userId = "system"; // replace with logged-in user fetch logic
        List<MaterialPricing> updatedList = new ArrayList<>();

        for (MaterialPricingRequestDto dto : dtos) {
            MaterialPricing pricing = materialPricingRepository.findById(dto.getUserIdSku())
                    .orElseThrow(() -> new ResourceNotFoundException("Pricing not found for userIdSku: " + dto.getUserIdSku()));

            pricing.setMrp(dto.getMrp());
            pricing.setDiscount(dto.getDiscount());
            pricing.setAmount(dto.getAmount());
            pricing.setGst(dto.getGst());
            pricing.setUpdatedBy(userId);
            pricing.setUpdatedDate(LocalDateTime.now());

            updatedList.add(materialPricingRepository.save(pricing));
        }

        return updatedList;
    }

    public Page<MaterialPricing> get(String userIdSku, Double mrp, Double gst,String updatedBy, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaterialPricing> query = cb.createQuery(MaterialPricing.class);
        Root<MaterialPricing> root = query.from(MaterialPricing.class);

        List<Predicate> predicates = new ArrayList<>();
        if (userIdSku != null && !userIdSku.trim().isEmpty()) {
            predicates.add(cb.equal(root.get("userIdSku"), userIdSku));
        }
        if (mrp != null) {
            predicates.add(cb.equal(root.get("mrp"), mrp));
        }
        if (gst != null) {
            predicates.add(cb.equal(root.get("gst"), gst));
        }
        if (updatedBy != null) {
            predicates.add(cb.equal(root.get("updatedBy"), updatedBy));
        }

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        TypedQuery<MaterialPricing> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MaterialPricing> countRoot = countQuery.from(MaterialPricing.class);

        List<Predicate> countPredicates = new ArrayList<>();
        if (userIdSku != null && !userIdSku.trim().isEmpty()) {
            countPredicates.add(cb.equal(countRoot.get("userIdSku"), userIdSku));
        }
        if (mrp != null) {
            countPredicates.add(cb.equal(countRoot.get("mrp"), mrp));
        }
        if (gst != null) {
            countPredicates.add(cb.equal(countRoot.get("gst"), gst));
        }

        countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }

    
    private static class UserInfo {
        String userId;

        UserInfo(String userId) {
            this.userId = userId;
        }
    }

    private UserInfo getLoggedInUserInfo(RegSource regSource) {
        String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
        Collection<? extends GrantedAuthority> loggedInRole = AuthDetailsProvider.getLoggedRole();

        List<String> roleNames = loggedInRole.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());

        if (!roleNames.contains("MS")) {
            throw new ResourceNotFoundException(
                "Only MaterialSupplierQuotation(MS) role is allowed. Found roles: " + roleNames);
        }

        UserType userType = UserType.MS;

        MaterialSupplierQuotationUser ms = materialSupplierQuotationUserDAO
                .findByEmailAndUserTypeAndRegSource(loggedInUserEmail, userType, regSource)
                .orElseThrow(() -> new ResourceNotFoundException("MS not found: " + loggedInUserEmail));

        String userId = ms.getBodSeqNo();
        return new UserInfo(userId);
    }
}

