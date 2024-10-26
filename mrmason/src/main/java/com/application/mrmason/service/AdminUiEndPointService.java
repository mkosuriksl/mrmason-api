package com.application.mrmason.service;

import java.util.List;

import com.application.mrmason.dto.AdminUiEndPointDto;
import com.application.mrmason.dto.ResponseAdminUiEndPointDto;
import com.application.mrmason.entity.AdminUiEndPointEntity;

public interface AdminUiEndPointService {

    ResponseAdminUiEndPointDto<AdminUiEndPointEntity> save(AdminUiEndPointDto dto);

    ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>> getById(String systemId, String ipUrlToUi);

    ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>> getAll();

    ResponseAdminUiEndPointDto<AdminUiEndPointEntity> update(AdminUiEndPointDto dto, String oldIpUrlToUi);

}
