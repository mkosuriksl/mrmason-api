package com.application.mrmason.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.mrmason.dto.GenericResponse;
import com.application.mrmason.dto.OrderDetailsCustomerDto;
import com.application.mrmason.dto.OrderQtyUpdateDto;
import com.application.mrmason.dto.OrderRequestDto;
import com.application.mrmason.entity.CustomerRetailerOrderHdrEntity;
import com.application.mrmason.enums.OrderStatus;
import com.application.mrmason.service.CustomerOrderHandler;


@RestController
public class CustomerOrderPlaceController {

	@Autowired
	private CustomerOrderHandler customerOrderHandler;
//	@Autowired
//	private OrderUpdatedHandler orderUpdatedHandler;

	@PostMapping("/placeOrder")
	public ResponseEntity<GenericResponse<CustomerRetailerOrderHdrEntity>> placeOrder(
			@RequestBody OrderRequestDto orderRequestDto) {

		CustomerRetailerOrderHdrEntity orderHdr = customerOrderHandler.placeOrder(orderRequestDto);
		GenericResponse<CustomerRetailerOrderHdrEntity> response = new GenericResponse<>("Place Ordered is successfully",true,
				orderHdr);
		return ResponseEntity.ok(response);

	}

	@GetMapping("/order/details")
	public ResponseEntity<List<OrderDetailsCustomerDto>> getOrderDetailsAndCustomer(
			@RequestParam(required = false) OrderStatus orderStatus, @RequestParam(required = false) String orderId, @RequestParam(required = false) String customerNumber,
			@RequestParam(required = false) String customerEmail,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam(required = false) String customerId,
			@RequestParam(required = false)String retailerId)
			throws InstantiationException, IllegalAccessException {

		return ResponseEntity.ok(customerOrderHandler.getOrderDetailsAndCustomerByParams(orderStatus, orderId,
				customerNumber, customerEmail, fromDate, toDate,customerId,retailerId));
	}
	
	@PutMapping("/update-order-qty")
	public ResponseEntity<GenericResponse<OrderQtyUpdateDto>> updateOrderQty(@RequestBody OrderQtyUpdateDto dto) {
		try {
			customerOrderHandler.updateOrderQty(dto); // method still returns void or String
			GenericResponse<OrderQtyUpdateDto> response = new GenericResponse<>("Updated successfully",true, dto);
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			GenericResponse<OrderQtyUpdateDto> response = new GenericResponse<>( e.getMessage(),false, null);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			GenericResponse<OrderQtyUpdateDto> response = new GenericResponse<>( "Something went wrong", false,null);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping ("/get-orderId-by-search")
	public ResponseEntity<Map<String, Object>> findOrderDetailsByOrderId(@RequestParam(required = false) String orderId,
			@RequestParam(required = false)String customerId,@RequestParam(required = false)String retailerId) {
		return ResponseEntity.ok(customerOrderHandler.getOrderDetailsByOrderId(orderId,customerId,retailerId));
	}

}
