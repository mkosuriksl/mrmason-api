package com.application.mrmason.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "work_progress_details1")
public class WorkProgressDetails {

	@Id
	@Column(name = "order_no_date", nullable = false, unique = true)
	private String orderNoDate;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "work_description")
    private String workDescription;

    @Column(name = "date_of_work")
    private Date dateOfWork;

    @Column(name = "no_of_resource")
    private String noOfResource;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "sub_task_id")
    private String subTaskId;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "pc_work_completion")
    private Integer pcWorkCompletion;

    @PrePersist
    private void prePersist() {
        if (this.orderNo != null) {
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            this.orderNoDate = this.orderNo + "_" + timestamp;
        } else {
            throw new IllegalStateException("orderNo cannot be null when persisting WorkProgressDetails");
        }
    }

}
