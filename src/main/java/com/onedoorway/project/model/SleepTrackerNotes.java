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
@Table(
        name = "sleep_tracker_notes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "report_date"})})
public class SleepTrackerNotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sleep_report_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "first_slot")
    private String firstSlot;

    @Column(name = "first_UpdatedBy")
    private String firstUpdatedBy;

    @Column(name = "second_slot")
    private String secondSlot;

    @Column(name = "second_UpdatedBy")
    private String secondUpdatedBy;

    @Column(name = "third_slot")
    private String thirdSlot;

    @Column(name = "third_UpdatedBy")
    private String thirdUpdatedBy;

    @Column(name = "fourth_slot")
    private String fourthSlot;

    @Column(name = "fourth_UpdatedBy")
    private String fourthUpdatedBy;

    @Column(name = "fifth_slot")
    private String fifthSlot;

    @Column(name = "fifth_UpdatedBy")
    private String fifthUpdatedBy;

    @Column(name = "sixth_slot")
    private String sixthSlot;

    @Column(name = "sixth_UpdatedBy")
    private String sixthUpdatedBy;

    @Column(name = "seventh_slot")
    private String seventhSlot;

    @Column(name = "seventh_UpdatedBy")
    private String seventhUpdatedBy;

    @Column(name = "eighth_slot")
    private String eighthSlot;

    @Column(name = "eighth_UpdatedBy")
    private String eighthUpdatedBy;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = " last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
