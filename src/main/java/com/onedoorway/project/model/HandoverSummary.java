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
@Table(name = "handover_summary")
public class HandoverSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hand_over_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "house_id", referencedColumnName = "house_id", nullable = false)
    private House house;

    @Column(name = "handOver_date")
    private LocalDate handoverDate;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "handover_time")
    private LocalDateTime handoverTime;

    @Column(name = "handOver_shift")
    @Enumerated(EnumType.ORDINAL)
    private HandoverShift handoverShift;

    @ManyToOne
    @JoinColumn(name = " handOver_by", referencedColumnName = "user_id", nullable = false)
    private User handoverById;

    @ManyToOne
    @JoinColumn(name = " handOver_to", referencedColumnName = "user_id", nullable = false)
    private User handoverToId;

    @Column(name = " behaviours_summary", columnDefinition = "TEXT")
    private String behaviourSummary;

    @Column(name = "sleep_summary", columnDefinition = "TEXT")
    private String sleepSummary;

    @Column(name = "food_summary", columnDefinition = "TEXT")
    private String foodSummary;

    @Column(name = "toileting_summary", columnDefinition = "TEXT")
    private String toiletingSummary;

    @Column(name = "activities_summary", columnDefinition = "TEXT")
    private String activitiesSummary;

    @Column(name = "communications", columnDefinition = "TEXT")
    private String communications;

    @Column(name = "top_priorities", columnDefinition = "TEXT")
    private String topPriorities;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "people_attended", columnDefinition = "TEXT")
    private String peopleAttended;

    @Column(name = "places_visited", columnDefinition = "TEXT")
    private String placesVisited;

    @Column(name = "things_later", columnDefinition = "TEXT")
    private String thingsLater;

    @Column(name = "last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
