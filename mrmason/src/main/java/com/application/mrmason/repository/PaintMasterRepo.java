package com.application.mrmason.repository;

import com.application.mrmason.entity.PaintMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PaintMasterRepo extends JpaRepository<PaintMaster, Integer> {

   // List<PaintMaster> findByIdAndBrand(int colorCode, String brand);

}
