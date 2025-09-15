package com.application.mrmason.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.MaterialSupplierDto;
import com.application.mrmason.dto.ResponseGetMasterDto;
import com.application.mrmason.entity.MaterialMaster;
import com.application.mrmason.entity.MaterialPricing;
import com.application.mrmason.entity.MaterialSupplierQuotationUser;
import com.application.mrmason.repository.MaterialMasterRepository;
import com.application.mrmason.repository.MaterialPricingRepository;
import com.application.mrmason.repository.MaterialSupplierQuotationUserDAO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class MaterialHomeService {

	@Autowired
	private MaterialSupplierQuotationUserDAO materialSupplierQuotationUserDAO;

	@Autowired
	private MaterialMasterRepository masterRepo;

	@Autowired
	private MaterialPricingRepository pricingRepo;

	@PersistenceContext
	private EntityManager entityManager;

	public ResponseGetMasterDto getMaterialsWithPagination(String location, String serviceCategory,
			String productCategory, String productSubCategory, String brand, String model, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);

		// 1. Get suppliers by location
		List<MaterialSupplierQuotationUser> supplierEntities = materialSupplierQuotationUserDAO
				.findByLocationContaining(location);

		// Map to DTO
		List<MaterialSupplierDto> suppliers = supplierEntities.stream().map(s -> {
			MaterialSupplierDto dto = new MaterialSupplierDto();
			dto.setBodSeqNo(s.getBodSeqNo());
			dto.setName(s.getName());
			dto.setBusinessName(s.getBusinessName());
			dto.setMobile(s.getMobile());
			dto.setEmail(s.getEmail());
			dto.setAddress(s.getAddress());
			dto.setCity(s.getCity());
			dto.setDistrict(s.getDistrict());
			dto.setState(s.getState());
			dto.setLocation(s.getLocation());
			return dto;
		}).toList();

		if (suppliers.isEmpty()) {
			ResponseGetMasterDto emptyResponse = new ResponseGetMasterDto();
			emptyResponse.setMessage("No suppliers found for location: " + location);
			emptyResponse.setStatus(false);
			emptyResponse.setMaterialMasters(Collections.emptyList());
			emptyResponse.setMaterialSupplier(Collections.emptyList());
			emptyResponse.setMasterPricing(Collections.emptyList());
			emptyResponse.setCurrentPage(page);
			emptyResponse.setPageSize(size);
			emptyResponse.setTotalElements(0);
			emptyResponse.setTotalPages(0);
			return emptyResponse;
		}

		List<String> userIds = supplierEntities.stream().map(MaterialSupplierQuotationUser::getBodSeqNo).toList();

		// 2. Fetch MaterialMaster using CriteriaBuilder with pagination
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MaterialMaster> query = cb.createQuery(MaterialMaster.class);
		Root<MaterialMaster> root = query.from(MaterialMaster.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(root.get("userId").in(userIds));

		if (serviceCategory != null && !serviceCategory.isEmpty())
			predicates.add(cb.equal(root.get("serviceCategory"), serviceCategory));
		if (productCategory != null && !productCategory.isEmpty())
			predicates.add(cb.equal(root.get("productCategory"), productCategory));
		if (productSubCategory != null && !productSubCategory.isEmpty())
			predicates.add(cb.equal(root.get("productSubCategory"), productSubCategory));
		if (brand != null && !brand.isEmpty())
			predicates.add(cb.equal(root.get("brand"), brand));
		if (model != null && !model.isEmpty())
			predicates.add(cb.equal(root.get("model"), model));

		query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
		TypedQuery<MaterialMaster> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult((int) pageable.getOffset());
		typedQuery.setMaxResults(pageable.getPageSize());
		List<MaterialMaster> masterList = typedQuery.getResultList();

		// 3. Count query for pagination
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<MaterialMaster> countRoot = countQuery.from(MaterialMaster.class);
		List<Predicate> countPredicates = new ArrayList<>();
		countPredicates.add(countRoot.get("userId").in(userIds));

		if (serviceCategory != null && !serviceCategory.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("serviceCategory"), serviceCategory));
		if (productCategory != null && !productCategory.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("productCategory"), productCategory));
		if (productSubCategory != null && !productSubCategory.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("productSubCategory"), productSubCategory));
		if (brand != null && !brand.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("brand"), brand));
		if (model != null && !model.isEmpty())
			countPredicates.add(cb.equal(countRoot.get("model"), model));

		countQuery.select(cb.count(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));
		Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		// 4. Fetch pricing for current page SKUs
		List<String> skuList = masterList.stream().map(MaterialMaster::getUserIdSku).toList();
		List<MaterialPricing> pricingList = pricingRepo.findByUserIdSkuIn(skuList);

		// 5. Build Response DTO
		ResponseGetMasterDto responseDto = new ResponseGetMasterDto();
		responseDto.setMessage("Material Master is retrieved successfully.");
		responseDto.setStatus(true);
		responseDto.setMaterialMasters(masterList);
		responseDto.setMaterialSupplier(suppliers);
		responseDto.setMasterPricing(pricingList);
		responseDto.setCurrentPage(page);
		responseDto.setPageSize(size);
		responseDto.setTotalElements(totalElements);
		responseDto.setTotalPages((int) Math.ceil((double) totalElements / size));

		return responseDto;
	}

}
