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
@Entity
@Table(
        name = "case_notes",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"client_id", "start_time", "end_time", "note_date"})
        })
public class CaseNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_note_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "category_id", referencedColumnName = "lookup_id")
    private Lookup category;

    @Column(name = "subject")
    private String subject;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;

    @Column(name = "note_date")
    private LocalDate noteDate;

    @Column(name = "last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
