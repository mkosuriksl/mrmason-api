package com.application.mrmason.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_services_projects")
@Builder
public class SpServiceWithNoOfProject {
	@Id
	private String userServicesId;
	private String projectsCompleted;
	private String ongoingProjects;
	
}
