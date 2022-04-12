package com.onedoorway.project.model;

import java.time.Instant;
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
@Entity(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incident_id")
    private long id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = "raised_for")
    @Enumerated(EnumType.ORDINAL)
    private RaisedFor raisedFor;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "escalated")
    private boolean escalated;

    @ManyToOne
    @JoinColumn(name = "escalated_to", referencedColumnName = "user_id", nullable = true)
    private User escalatedTo;

    @Column(name = "date")
    private LocalDateTime dateOccurred;

    @Column(name = "location")
    private String location;

    @Column(name = "exact_location")
    private String exactLocation;

    @ManyToOne
    @JoinColumn(name = "reported_by", referencedColumnName = "user_id", nullable = false)
    private User reportedBy;

    @Column(name = "injured_given_name")
    private String injuredGivenName;

    @Column(name = "injured_family_name")
    private String injuredFamilyName;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "closed_by")
    private String closedBy;

    @Column(name = "created_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @Column(name = "witness_name")
    private String witnessName;

    @Column(name = "witness_designation")
    private String witnessDesignation;

    @Column(name = "follow_up_responsibility")
    private String followUpResponsibility;

    @Column(name = "police_report")
    @Enumerated(EnumType.ORDINAL)
    private YesNo policeReport;

    @Column(name = "police_name")
    private String policeName;

    @Column(name = "police_number")
    private String policeNumber;

    @Column(name = "police_station")
    private String policeStation;

    @Column(name = "before_incident", columnDefinition = "TEXT")
    private String beforeIncident;

    @Column(name = "immediate_action", columnDefinition = "TEXT")
    private String immediateAction;

    @Column(name = "reportable_to_NDIS")
    @Enumerated(EnumType.ORDINAL)
    private YesNo reportableToNDIS;

    @Column(name = "reportable_to_worksafe")
    @Enumerated(EnumType.ORDINAL)
    private YesNo reportableToWorksafe;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "category_id", referencedColumnName = "lookup_id")
    private Lookup category;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "type_id", referencedColumnName = "lookup_id")
    private Lookup type;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "classification_id", referencedColumnName = "lookup_id")
    private Lookup classification;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "incident_house_id", referencedColumnName = "house_id")
    private House house;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;
}
