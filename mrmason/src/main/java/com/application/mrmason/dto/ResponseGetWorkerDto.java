package com.application.mrmason.dto;

import java.util.List;

import com.application.mrmason.entity.SPWorkAssignment;
import com.application.mrmason.entity.SpWorkers;

import lombok.Data;
@Data
public class ResponseGetWorkerDto {
	private String message;
	private boolean status;
	private List<SpWorkers> workersData;
	private List<SPWorkAssignment> spworkerAssignment;
	private Userdto userData;
}