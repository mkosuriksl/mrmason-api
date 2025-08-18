package com.application.mrmason.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.application.mrmason.entity.CMaterialReqHeaderDetailsEntity;

@Repository
public interface CMaterialReqHeaderDetailsRepository
                extends JpaRepository<CMaterialReqHeaderDetailsEntity, String> {

        @Query("SELECT c FROM CMaterialReqHeaderDetailsEntity c " +
                        "WHERE (:cMatRequestIdLineid IS NULL OR c.cMatRequestIdLineid = :cMatRequestIdLineid) " +
                        "AND (:cMatRequestId IS NULL OR c.cMatRequestId = :cMatRequestId) " +
                        "AND (:materialCategory IS NULL OR c.materialCategory = :materialCategory) " +
                        "AND (:brand IS NULL OR c.brand = :brand) " +
                        "AND (:itemName IS NULL OR c.itemName = :itemName) " +
                        "AND (:itemSize IS NULL OR c.itemSize = :itemSize) " +
                        "AND (:qty IS NULL OR c.qty = :qty) " +
                        "AND (:orderDate IS NULL OR c.orderDate = :orderDate)"+
                        "AND (:requestedBy IS NULL OR c.requestedBy = :requestedBy) " +
                        "AND (:updatedDate IS NULL OR c.updatedDate = :updatedDate)")
        List<CMaterialReqHeaderDetailsEntity> findMaterialRequestsByFilters(
                        @Param("cMatRequestIdLineid") String cMatRequestIdLineid,
                        @Param("cMatRequestId") String cMatRequestId,
                        @Param("materialCategory") String materialCategory,
                        @Param("brand") String brand,
                        @Param("itemName") String itemName,
                        @Param("itemSize") String itemSize,
                        @Param("qty") Integer qty,
                        @Param("orderDate") LocalDate orderDate,
                        @Param("requestedBy") String requestedBy,
                        @Param("updatedDate") LocalDate updatedDate);

        @Query("SELECT c FROM CMaterialReqHeaderDetailsEntity c WHERE c.cMatRequestId = :cMatRequestId")
        List<CMaterialReqHeaderDetailsEntity> findByCMatRequestId(@Param("cMatRequestId") String cMatRequestId);

        @Query("SELECT DISTINCT d.cMatRequestId FROM CMaterialReqHeaderDetailsEntity d " +
        	       "WHERE (:brand IS NULL OR LOWER(d.brand) = LOWER(:brand)) " +
        	       "AND (:itemName IS NULL OR LOWER(d.itemName) = LOWER(:itemName)) " +
        	       "AND (:itemSize IS NULL OR LOWER(d.itemSize) = LOWER(:itemSize))")
        	List<String> findHeaderIdsByFilters(@Param("brand") String brand,
        	                                    @Param("itemName") String itemName,
        	                                    @Param("itemSize") String itemSize);


}