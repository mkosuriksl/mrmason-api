package com.application.mrmason.entity;

import org.hibernate.annotations.UpdateTimestamp;

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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_ui_end_point")
public class AdminUiEndPoint {
	@Id
	@Column(name = "system_id")
	private String systemId;
	
	@Column(name = "ip_url_to_ui")
	private String ipUrlToUi;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "updated_time")
	@UpdateTimestamp
	private String updatedTime;
}
