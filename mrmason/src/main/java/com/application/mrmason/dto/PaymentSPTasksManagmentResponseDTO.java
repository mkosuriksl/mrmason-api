package com.application.mrmason.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSPTasksManagmentResponseDTO {
    private String requestLineId;
    private String taskName;
    private Integer amount;
    private Integer workPersentage;
    private Integer amountPersentage;
    private String dailylaborPay;
    private String advancedPayment;
    private String updatedBy;
    private Date updatedDate;
    private String spId;
}
