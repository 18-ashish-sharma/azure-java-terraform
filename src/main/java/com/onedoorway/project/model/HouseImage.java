package com.onedoorway.project.model;

import java.time.Instant;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "house_image")
public class HouseImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_image_id")
    private long id;

    @Column(name = "house_code", unique = true)
    private String houseCode;

    @Column(name = "image_blob_name")
    private String imageBlobName;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;

    @Column(name = " last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
