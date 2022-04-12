package com.onedoorway.project.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "client_reports")
public class ClientReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_report_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "lookup_id", referencedColumnName = "lookup_id", nullable = false)
    private Lookup lookup;

    @Column(name = "toggle")
    private Boolean toggle;
}
