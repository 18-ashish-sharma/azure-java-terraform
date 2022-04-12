package com.onedoorway.project.model;

import java.time.Instant;
import java.time.LocalDate;
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
@Table(name = "miscellaneous_notes")
public class MiscellaneousNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "miscellaneous_note_id")
    private long id;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "category_id", referencedColumnName = "lookup_id")
    private Lookup category;

    @Column(name = "note_date")
    private LocalDate noteDate;

    @Column(name = "subject")
    private String subject;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "house")
    private String house;

    @Column(name = "client")
    private String client;

    @Column(name = "users")
    private String user;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;

    @Column(name = "last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
