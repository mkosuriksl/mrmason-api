package com.application.mrmason.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.application.mrmason.entity.GstInServiceUser;
import com.application.mrmason.enums.RegSource;

public interface GstInServiceUserService {
	public List<GstInServiceUser> saveGst(List<GstInServiceUser> users, RegSource regSource);

	public List<GstInServiceUser> updateGst(List<GstInServiceUser> users, RegSource regSource);
	
	public Page<GstInServiceUser> getGst(String bodSeqNo, String gst, String userId, RegSource regSource, Pageable pageable) throws AccessDeniedException ;
}
