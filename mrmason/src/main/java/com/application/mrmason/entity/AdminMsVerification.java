package com.application.mrmason.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admin_ms_verification")
public class AdminMsVerification{

	@Id
	@Column(name = "BOD_SEQ_NO")
	public String bodSeqNo;
	
	@Column(name = "STATUS")
	private String status = "new";

	@Column(name = "comment")
	public String comment;

	@Column(name = "updated_by")
	public String updateBy;

	@Column(name = "updated_date")
	public Date updatedDate;

	
}
