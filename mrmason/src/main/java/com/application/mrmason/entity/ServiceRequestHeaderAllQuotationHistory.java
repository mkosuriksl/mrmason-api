package com.application.mrmason.entity;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "service_request_all_quotation_updated_by_customer")
public class ServiceRequestHeaderAllQuotationHistory {

	@Id
    @Column(name = "quotation_id", unique = true)
    private String quotationId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "quoted_date")
    private Date quotedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SPWAStatus status;

    @Column(name = "sp_id")
    private String spId;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "user_type")
    private String userType; // For tracking who performed the update
}
