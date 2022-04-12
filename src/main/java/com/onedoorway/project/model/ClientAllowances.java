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
@Entity(name = "client_allowances")
public class ClientAllowances {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_allowances_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "cappedKMs", columnDefinition = "TEXT")
    private String cappedKMs;

    @Column(name = "concession_cards", columnDefinition = "TEXT")
    private String concessionCard;

    @Column(name = "kms", columnDefinition = "TEXT")
    private String kms;

    @Column(name = "grocery_spend", columnDefinition = "TEXT")
    private String grocerySpend;

    @Column(name = "budgetly_card_No", columnDefinition = "TEXT")
    private String budgetlyCardNo;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;
}
