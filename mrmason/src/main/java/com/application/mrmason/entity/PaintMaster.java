package com.application.mrmason.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paint_master")
public class PaintMaster {

    @Id
    @Column(name = "color_code")
    private int colorCode;
    @Column(name="color_image")
    private String colorImage;
    @Column(name = "wall_type")
    private String wallType;
    @Column(name = "updated_date")
    @UpdateTimestamp
    private Date updatedDate;
    @Column(name = "brand")
    private String brand;


}
