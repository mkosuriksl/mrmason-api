package com.application.mrmason.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.CustomerOrderDetailsRepo;
import com.application.mrmason.dto.CustomerOrderHdrRepo;
import com.application.mrmason.dto.OrderDetailsCustomerDto;
import com.application.mrmason.dto.OrderDetailsDto;
import com.application.mrmason.dto.OrderQtyUpdateDto;
import com.application.mrmason.dto.OrderRequestDto;
import com.application.mrmason.entity.CustomerOrderDetailsEntity;
import com.application.mrmason.entity.CustomerOrderHdrEntity;
import com.application.mrmason.entity.CustomerRegistration;
import com.application.mrmason.entity.CustomerRetailerOrderDetailsEntity;
import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;
import com.application.mrmason.entity.MaterialRequirementByRequest;
import com.application.mrmason.entity.UserType;
import com.application.mrmason.enums.OrderStatus;
import com.application.mrmason.enums.RegSource;
import com.application.mrmason.exceptions.ResourceNotFoundException;
import com.application.mrmason.repository.CustomerRegistrationRepo;
import com.application.mrmason.repository.CustomerRetailerOrderDetailsRepo;
import com.application.mrmason.repository.CustomerRetailerOrderHdrRepo;
import com.application.mrmason.repository.MaterialRequirementByRequestRepository;
import com.application.mrmason.security.AuthDetailsProvider;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class CustomerOrderHandler {

    @Autowired
    private CustomerRetailerOrderHdrRepo orderHdrRepo;
    @Autowired
    private CustomerRetailerOrderDetailsRepo orderDetailsRepo;
	@Autowired
	CustomerRegistrationRepo customerRegisterRepository;
	@Autowired
	private CustomerOrderHdrRepo customerCartHdrRepo;
	@Autowired
	private CustomerOrderDetailsRepo customerCartDetailsRepo;
	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	private MaterialRequirementByRequestRepository materialRequirementByRequestRepository;
	@Transactional
	public CustomerRetailerOrderHdrEntity placeOrder(OrderRequestDto dto) {
	    // 1️⃣ Fetch customer cart header
	    CustomerOrderHdrEntity cartHeader = customerCartHdrRepo.findById(dto.getCustomerCartOrderId())
	            .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

	    // 2️⃣ Create order header
	    CustomerRetailerOrderHdrEntity orderHdr = new CustomerRetailerOrderHdrEntity();
	    orderHdr.setCustomerCartOrderId(cartHeader.getOrderId());
	    orderHdr.setCustomerId(dto.getCustomerId());
	    String userId=cartHeader.getCustomerOrderDetailsEntities().get(0).getMsUserId();
	    orderHdr.setRetailerId(userId); // ✅ ensure this is set BEFORE generating orderId
	    orderHdr.setDeliveryMethod(dto.getDeliveryMethod());
	    orderHdr.setOrderStatus(OrderStatus.NEW);
	    orderHdr.setPaymentStatus(OrderStatus.PENDING);
	    orderHdr.setOrderUpdatedBy("System");
	    orderHdr.setOrderDate(LocalDate.now());
	    orderHdr.setOrderUpdatedDate(new Date());

	    // 2a️⃣ Generate orderId with userIdstoreId included
	    String sequenceNumber = String.format("%06d", new Random().nextInt(900000) + 100000);
	    orderHdr.setOrderId("INVOICE" +LocalDate.now().getYear()+ sequenceNumber + "_" + userId );

	    orderHdrRepo.save(orderHdr);

	    // 3️⃣ Fetch all cart details for this cart
	    List<CustomerOrderDetailsEntity> cartDetails =
	            customerCartDetailsRepo.findByOrderId(cartHeader.getOrderId());

	    // 4️⃣ Safely handle orderDetailsList from DTO
	    List<OrderDetailsDto> orderDetailsList =
	            Optional.ofNullable(dto.getOrderDetailsList()).orElse(Collections.emptyList());

	    // ✅ Only validate if frontend sent the list
	    if (!orderDetailsList.isEmpty() && orderDetailsList.size() != cartDetails.size()) {
	        throw new RuntimeException("Mismatch between cart items and order request items");
	    }

	    // 5️⃣ Map cart details to retailer order details
	    int counter = 1;
	    for (CustomerOrderDetailsEntity cartDetail : cartDetails) {
	        CustomerRetailerOrderDetailsEntity orderDetail = new CustomerRetailerOrderDetailsEntity();

	        // LineItemId = orderId + line counter
	        String formattedCounter = String.format("%04d", counter++);
	        String lineItemId = orderHdr.getOrderId() + "_" + formattedCounter;
	        orderDetail.setLineItemId(lineItemId);

	        // copy fields from cart
	        orderDetail.setCustomerCartOrderLineId(cartDetail.getOrderlineId());
	        orderDetail.setBrand(cartDetail.getBrand());
	        orderDetail.setOrderQty(cartDetail.getOrderQty());
	        orderDetail.setMrp(cartDetail.getMrp());
	        orderDetail.setDiscount(cartDetail.getDiscount());
	        orderDetail.setGst(cartDetail.getGst());
	        orderDetail.setTotalAmount(cartDetail.getTotal());
	        orderDetail.setSkuIdUserId(cartDetail.getSkuIdUserId());
	        orderDetail.setCustomerRetailerOrderHdr(orderHdr);
	        orderDetailsRepo.save(orderDetail);
	        
	        MaterialRequirementByRequest materialReq = new MaterialRequirementByRequest();
	        materialReq.setReqIdLineId(orderDetail.getLineItemId()); // orderlineId -> reqIdLineId
//	        materialReq.setThickness(cartDetail.getThickness() != null ? new BigDecimal(cartDetail.getThickness()) : null);
	        materialReq.setThickness(cartDetail.getThickness());
	        materialReq.setModelCode(cartDetail.getSkuIdUserId()); // skuIdUserId -> modelCode
	        materialReq.setModelName(cartDetail.getModelName());
	        materialReq.setBrand(cartDetail.getBrand());  
	        materialReq.setGst(cartDetail.getGst() != null ? new BigDecimal(cartDetail.getGst()) : null);
	        materialReq.setNoOfItems(cartDetail.getOrderQty());
	        materialReq.setAmount(cartDetail.getMrp() != null ? new BigDecimal(cartDetail.getMrp()) : null);
	        materialReq.setMaterialCategory(cartDetail.getMaterialCategory());
	        materialReq.setShape(cartDetail.getShape());
	        materialReq.setSizeInInch(cartDetail.getSize());
	        materialReq.setWidth(cartDetail.getWidth());
	        materialReq.setMaterialSubCategory(cartDetail.getMaterialSubCategory());
//	        materialReq.setLength(cartDetail);
	        materialReq.setUpdatedBy("System");
	        materialReq.setUpdatedDate(new Date());
	        materialReq.setStatus(OrderStatus.NEW.name());
	        materialRequirementByRequestRepository.save(materialReq);
	        
	        cartDetail.setStatus(OrderStatus.COMPLETED);
	        cartDetail.setUpdatedDate(new Date());
	        cartDetail.setUpdatedBy("System");
	    }

	    // 6️⃣ Update cart header
	    cartHeader.setStatus(OrderStatus.COMPLETED);
	    cartHeader.setOrderUpdatedDate(new Date());
	    cartHeader.setOrderUpdatedBy("System");

	    customerCartHdrRepo.save(cartHeader);
	    customerCartDetailsRepo.saveAll(cartDetails);

	    return orderHdr;
	}

	@Transactional
    public List<OrderDetailsCustomerDto> getOrderDetailsAndCustomerByParams(OrderStatus orderStatus,String orderId, String customerNumber, String customerEmail,
    		LocalDate fromDate, LocalDate toDate,String customerId,String retailerId) throws InstantiationException, IllegalAccessException {
        List<CustomerRetailerOrderHdrEntity> orderHdrList;

        if(customerNumber!=null ||customerEmail!=null){
        	List<CustomerRegistration> customerRegisters = customerRegisterRepository.findByUserMobileOrUserEmail(customerNumber, customerEmail);

        	List<String> customerIds = customerRegisters.stream()
        	        .map(CustomerRegistration::getUserid)
        	        .collect(Collectors.toList());

        	orderHdrList = orderHdrRepo.findByCustomerIdIn(customerIds);

        } else if (orderId != null) {
            CustomerRetailerOrderHdrEntity orderHdr = orderHdrRepo.findByOrderId(orderId).
                    orElseThrow(() -> new IllegalArgumentException("Order not found"));
            orderHdrList = new ArrayList<>();
            orderHdrList.add(orderHdr);
        }
        else if (fromDate != null && toDate != null) {
            orderHdrList = orderHdrRepo.findByOrderDateBetween(fromDate, toDate);
        }
        else if (orderStatus != null) {
            orderHdrList = orderHdrRepo.findByOrderStatus(orderStatus);
        }
        else if (retailerId != null) { // ✅ handle retailerId directly
            orderHdrList = orderHdrRepo.findByRetailerId(retailerId);
        }
        else   if (customerId != null) {
        	 orderHdrList = orderHdrRepo.findByCustomerId(customerId);
        }
        else {
            throw new IllegalArgumentException("At least one parameter must be provided");
        }
        return buildResultDtoList(orderHdrList);
        }
    private List<OrderDetailsCustomerDto> buildResultDtoList(List<CustomerRetailerOrderHdrEntity> orderHdrList) {
        List<OrderDetailsCustomerDto> resultDtoList = new ArrayList<>();
        for (CustomerRetailerOrderHdrEntity orderHdr : orderHdrList) {
            OrderDetailsCustomerDto resultDto = new OrderDetailsCustomerDto();
            resultDto.setOrderHdr(orderHdr);
            // Fetch Customer info
            CustomerRegistration customer = customerRegisterRepository.findByUserid(orderHdr.getCustomerId());
            resultDto.setCustomer(customer);
            resultDtoList.add(resultDto);
        }
        return resultDtoList;
    }
//
//
    @Transactional
    public String updateOrderQty(OrderQtyUpdateDto dto) {
        String loggedInUserEmail = AuthDetailsProvider.getLoggedEmail();
		Optional<CustomerRegistration> login = customerRegisterRepository.findByUserEmailAndUserTypeAndRegSource(loggedInUserEmail,UserType.EC,RegSource.MRMASON);
		if (login.isEmpty()) {
			throw new ResourceNotFoundException("Access denied. This API is restricted to customer users only.");
		}


        CustomerRetailerOrderDetailsEntity details = orderDetailsRepo.findById(dto.getLineItemId())
                .orElseThrow(() -> new IllegalArgumentException("Line item not found"));

        // Update only if value is different
        if (details.getOrderQty() != dto.getOrderQty()) {
            details.setOrderQty(dto.getOrderQty());
        }

        // Update header
        CustomerRetailerOrderHdrEntity header = details.getCustomerRetailerOrderHdr();
        if (header != null) {
            header.setOrderUpdatedBy(login.get().getUserid());
            header.setOrderUpdatedDate(new Date());
            header.setOrderStatus(dto.getOrderStatus());
            orderHdrRepo.save(header);
        }

        orderDetailsRepo.save(details);

        return "Order quantity and header updated successfully.";
    }

    public Map<String, Object> getOrderDetailsByOrderId(String orderId, String customerId, String retailerId) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<CustomerRetailerOrderHdrEntity> root = query.from(CustomerRetailerOrderHdrEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (orderId != null && !orderId.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("orderId")), "%" + orderId.toLowerCase() + "%"));
        }
        if (customerId != null && !customerId.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("customerId")), "%" + customerId.toLowerCase() + "%"));
        }
        if (retailerId != null && !retailerId.isBlank()) {
            predicates.add(cb.equal(cb.trim(root.get("retailerId")), retailerId.trim()));
        }

        // ✅ If no params passed → return empty response
        if (predicates.isEmpty()) {
            return Map.of(
                "message", "No search parameters provided",
                "status", false,
                "totalCount", 0,
                "data", List.of()
            );
        }

        query.multiselect(
            root.get("orderId"),
            root.get("customerId"),
            root.get("retailerId")
        );
        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Object[]> resultList = entityManager.createQuery(query).getResultList();

        List<Map<String, Object>> responseData = resultList.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", obj[0]);
            map.put("customerId", obj[1]);
            map.put("retailerId", obj[2]);
            return map;
        }).collect(Collectors.toList());

        return Map.of(
            "message", "orderId report fetched successfully",
            "status", true,
            "totalCount", responseData.size(),
            "data", responseData
        );
    }
}
