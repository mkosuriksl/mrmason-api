package com.application.mrmason.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.CustomerGetOrderResponseDTO;
import com.application.mrmason.dto.CustomerOrderRequestDto;
import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.UpdateCustomerOrderRequestDto;
import com.application.mrmason.entity.CustomerOrderDetailsEntity;
import com.application.mrmason.entity.CustomerOrderHdrEntity;
import com.application.mrmason.service.CustomerOrderMethodHandler;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class CustomerOrderMethodController {

    private final PaymentSPTasksManagmentController paymentSPTasksManagmentController;

	@Autowired
	private CustomerOrderMethodHandler orderMethodHandler;

    CustomerOrderMethodController(PaymentSPTasksManagmentController paymentSPTasksManagmentController) {
        this.paymentSPTasksManagmentController = paymentSPTasksManagmentController;
    }

	@PostMapping("/customer-cart/add")
	public ResponseEntity<GenericResponse<CustomerOrderHdrEntity>> createCustomerOrderMethod(
			@RequestBody CustomerOrderRequestDto requestDto) {
		try {
			CustomerOrderHdrEntity orderHdr = orderMethodHandler.ceateCustomerOrderMethod(requestDto);

			GenericResponse<CustomerOrderHdrEntity> response = new GenericResponse<>(

					"Customer Order placed successfully", true, orderHdr);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			GenericResponse<CustomerOrderHdrEntity> response = new GenericResponse<>(

					"Failed to place order: " + e.getMessage(), false, null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PutMapping("/customer-cart/update")
	public ResponseEntity<?> updateOrderLine(@RequestBody UpdateCustomerOrderRequestDto dto) {
		try {
			List<CustomerOrderDetailsEntity> updated = orderMethodHandler.updateOrderDetails(dto);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	
	@GetMapping("/customer-cart/get")
	public GenericResponse<List<CustomerGetOrderResponseDTO>> getOrders(
	        @RequestParam(required = false) String cId,
	        @RequestParam(required = false) String orderId,
	        @RequestParam(required = false) String orderlineId,
	        @RequestParam(required = false) String skuIdUserId,
	        @RequestParam(required = false) String fromDate,
	        @RequestParam(required = false) String toDate,
			@RequestParam(required = false) String msUserId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {
	    return orderMethodHandler.getOrderDetail(cId, orderId, orderlineId,skuIdUserId, fromDate, toDate, msUserId, page, size);
	}

	@DeleteMapping("/customer-cart/delete")
	public ResponseEntity<Map<String, Object>> deleteOrder(
	        @RequestParam String orderId,
	        @RequestParam String orderlineId) {
	    Map<String, Object> response = new HashMap<>();
	    try {
	        String message = orderMethodHandler.deleteOrderLineAndHeader(orderId, orderlineId);
	        response.put("status", true);
	        response.put("message", message);
	        return ResponseEntity.ok(response);
	    } catch (EntityNotFoundException e) {
	        response.put("status", false);
	        response.put("message", e.getMessage());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    } catch (Exception e) {
	        response.put("status", false);
	        response.put("message", "Error deleting order.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}

	@GetMapping("/get-customer-cart")
	public GenericResponse<List<CustomerGetOrderResponseDTO>> getOrdersByCustomerId(
	        @RequestParam String cId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {

	    return orderMethodHandler.getOrderDetailByCustomerId(cId, page, size);
	}

}
