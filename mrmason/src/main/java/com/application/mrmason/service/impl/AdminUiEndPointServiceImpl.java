package com.application.mrmason.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mrmason.dto.AdminUiEndPointDto;
import com.application.mrmason.dto.ResponseAdminUiEndPointDto;
import com.application.mrmason.entity.AdminUiEndPointEntity;
import com.application.mrmason.entity.AdminUiEndPointId;
import com.application.mrmason.repository.AdminUiEndPointRepository;
import com.application.mrmason.service.AdminUiEndPointService;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Service
public class AdminUiEndPointServiceImpl implements AdminUiEndPointService {

    private static final Logger logger = LoggerFactory.getLogger(AdminUiEndPointServiceImpl.class);

    @Autowired
    private AdminUiEndPointRepository repository;

    public ResponseAdminUiEndPointDto<AdminUiEndPointEntity> save(AdminUiEndPointDto dto) {
        logger.info("Saving Admin UI EndPoint with systemId: {}", dto.getSystemId());
        AdminUiEndPointEntity entity = new AdminUiEndPointEntity(
                dto.getSystemId(),
                dto.getIpUrlToUi(),
                dto.getUpdatedBy());
        AdminUiEndPointEntity savedEntity = repository.save(entity);
        logger.debug("Saved entity: {}", savedEntity);
        return new ResponseAdminUiEndPointDto<>("Admin Ui End Point added succesfully", true, savedEntity);
    }

    @Override
    public ResponseAdminUiEndPointDto<AdminUiEndPointEntity> update(AdminUiEndPointDto dto, String oldIpUrlToUi) {
        logger.info("Updating Admin UI EndPoint with systemId: {} and old ipUrlToUi: {}", dto.getSystemId(),
                oldIpUrlToUi);

        AdminUiEndPointId originalId = new AdminUiEndPointId(dto.getSystemId(), oldIpUrlToUi);

        logger.info("Looking for AdminUiEndPointEntity with systemId: {} and old ipUrlToUi: {}",
                originalId.getSystemId(), originalId.getIpUrlToUi());
        Optional<AdminUiEndPointEntity> entityOpt = repository.findById(originalId);

        if (entityOpt.isPresent()) {
            AdminUiEndPointEntity existingEntity = entityOpt.get();

            logger.info("Entity found. Deleting entity with systemId: {} and old ipUrlToUi: {}",
                    existingEntity.getId().getSystemId(), existingEntity.getId().getIpUrlToUi());
            repository.delete(existingEntity);

            AdminUiEndPointEntity newEntity = new AdminUiEndPointEntity(dto.getSystemId(), dto.getIpUrlToUi(),
                    dto.getUpdatedBy());

            logger.info("Saving new entity with systemId: {} and new ipUrlToUi: {}", dto.getSystemId(),
                    dto.getIpUrlToUi());
            AdminUiEndPointEntity savedEntity = repository.save(newEntity);
            logger.debug("Saved new entity: {}", savedEntity);

            return new ResponseAdminUiEndPointDto<>("Admin Ui End Point updated successfully", true, savedEntity);
        } else {

            logger.error("Entity not found for systemId: {} and old ipUrlToUi: {}", dto.getSystemId(), oldIpUrlToUi);
            return new ResponseAdminUiEndPointDto<>("Admin Ui End Point not updated", false, null);
        }
    }

    @Override
    public ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>> getById(String systemId, String ipUrlToUi) {
        logger.info("Fetching Admin UI EndPoint with systemId: {} and ipUrlToUi: {}", systemId, ipUrlToUi);

        if (systemId == null && ipUrlToUi == null) {
            logger.warn("At least one of systemId or ipUrlToUi must be provided");
            return new ResponseAdminUiEndPointDto<>("At least one of systemId or ipUrlToUi must be provided", false,
                    null);
        }

        if (systemId != null && ipUrlToUi != null) {
            Optional<AdminUiEndPointEntity> entityOpt = repository.findById(new AdminUiEndPointId(systemId, ipUrlToUi));
            return entityOpt
                    .map(entity -> new ResponseAdminUiEndPointDto<>("Admin UI EndPoint Retrieved successfully", true,
                            List.of(entity)))
                    .orElseGet(() -> new ResponseAdminUiEndPointDto<>("Admin UI EndPoint Not Retrieved", false, null));
        }

        if (systemId != null) {
            List<AdminUiEndPointEntity> entities = repository.findAllById_SystemId(systemId);
            if (!entities.isEmpty()) {
                return new ResponseAdminUiEndPointDto<>("Admin UI EndPoints Retrieved successfully", true, entities);
            } else {
                return new ResponseAdminUiEndPointDto<>("No Admin UI EndPoints found for the provided systemId", false,
                        null);
            }
        }

        List<AdminUiEndPointEntity> entities = repository.findAllById_IpUrlToUi(ipUrlToUi);
        if (!entities.isEmpty()) {
            return new ResponseAdminUiEndPointDto<>("Admin UI EndPoints Retrieved successfully", true, entities);
        } else {
            return new ResponseAdminUiEndPointDto<>("No Admin UI EndPoints found for the provided ipUrlToUi", false,
                    null);
        }
    }

    @Override
    public ResponseAdminUiEndPointDto<List<AdminUiEndPointEntity>> getAll() {
        logger.info("Fetching all Admin UI EndPoints");

        List<AdminUiEndPointEntity> entities = repository.findAll();

        if (!entities.isEmpty()) {
            return new ResponseAdminUiEndPointDto<>("All Admin Ui End Points Retrieved successfully", true, entities);
        } else {
            return new ResponseAdminUiEndPointDto<>("Admin Ui End Point Not Retrieved", false, null);
        }
    }

}
