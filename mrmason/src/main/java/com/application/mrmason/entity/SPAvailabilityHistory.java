package com.application.mrmason.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_person_availability_history")
public class SPAvailabilityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEP_HIS_SEQ_NO")
    private int sepHisSeqNo;

    @Column(name = "SERVICE_PERSON_ID")
    private String bodSeqNo;

    @Column(name = "AVAILABILITY")
    private String availability;

    @Column(name = "LOCATION")
    private String address;

    @Column(name = "DATETIME_OF_UPDATE")
    private String dateTimeOfUpdate;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt = LocalDateTime.now();
}

