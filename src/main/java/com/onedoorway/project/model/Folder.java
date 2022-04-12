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
@Entity(name = "folders")
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false)
    private Client client;

    @Column(name = "folder_name")
    private String folderName;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private NoticeStatus status;

    @Column(name = "last_UpdatedBy")
    private String lastUpdatedBy;

    @Column(name = " last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
