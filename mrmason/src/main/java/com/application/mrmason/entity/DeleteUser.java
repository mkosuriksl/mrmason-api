package com.application.mrmason.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "deleted_users")
public class DeleteUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "candidate_id", nullable = false)
	private String  candidateId;

	@Column(name = "deleted_date", nullable = false)
	public LocalDateTime deletedDate;

	@Column(name = "is_deactivated", nullable = false)
	private boolean deactivated = true;

	@Column(name = "reason_to_delete")
	public String deleteReason;

}
