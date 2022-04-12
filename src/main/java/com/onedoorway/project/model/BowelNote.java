package com.onedoorway.project.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Entity(name = "bowel_notes")
public class BowelNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bowel_note_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "size_type")
    @Enumerated(EnumType.ORDINAL)
    private Size size;

    @Column(name = "type_1")
    private Boolean type1;

    @Column(name = "type_2")
    private Boolean type2;

    @Column(name = "type_3")
    private Boolean type3;

    @Column(name = "type_4")
    private Boolean type4;

    @Column(name = "type_5")
    private Boolean type5;

    @Column(name = "type_6")
    private Boolean type6;

    @Column(name = "type_7")
    private Boolean type7;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "record_time")
    private LocalDateTime recordTime;

    @Column(name = "last_Uploaded_By")
    private String lastUploadedBy;

    @Column(name = "last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
