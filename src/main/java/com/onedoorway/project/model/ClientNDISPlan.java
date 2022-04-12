package com.onedoorway.project.model;

import java.time.LocalDate;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "client_ndis_plan",
        uniqueConstraints = {
            @UniqueConstraint(
                    columnNames = {"client_id", "start_date", "end_date", "funding_type", "level"})
        })
public class ClientNDISPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_ndis_plan_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "funding_type")
    private String fundingType;

    @Column(name = "level")
    private String level;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "support_document")
    private String supportDocument;

    @Column(name = "other_document")
    private String otherDocument;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;
}
