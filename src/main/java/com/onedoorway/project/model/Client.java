package com.onedoorway.project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private long id;

    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "dob")
    private LocalDate dob;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address1")
    private String addrLine1;

    @Column(name = "address2")
    private String addrLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "post_code")
    @Pattern(regexp = "\\d{4}")
    private String postCode;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "medicare_no")
    private long medicareNo;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "health_fund")
    private Boolean healthFund;

    @Column(name = "center_link_no")
    private String centerLinkNo;

    @Column(name = "medicare_card_name")
    private String medicareCardName;

    @Column(name = "individual_reference_number")
    private String individualReferenceNumber;

    @Column(name = "ndis_number", columnDefinition = "TEXT")
    private String ndisNumber;

    @Column(name = "photo", columnDefinition = "TEXT")
    private String photo;

    @Column(name = "identity")
    private String identity;

    @Column(name = "culture")
    private String culture;

    @Column(name = "language")
    private String language;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "mobility", columnDefinition = "TEXT")
    private String mobility;

    @Column(name = "communication", columnDefinition = "TEXT")
    private String communication;

    @Column(name = "medication_support", columnDefinition = "TEXT")
    private String medicationSupport;

    @Column(name = "transportation", columnDefinition = "TEXT")
    private String transportation;

    @Column(name = "justice_orders", columnDefinition = "TEXT")
    private String justiceOrders;

    @Column(name = "support_ratio", columnDefinition = "TEXT")
    private String supportRatio;

    @Column(name = "shift_times", columnDefinition = "TEXT")
    private String shiftTimes;

    @Column(name = "support_worker_specs", columnDefinition = "TEXT")
    private String supportWorkerSpecs;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "house_id")
    @JsonBackReference
    private House house;
}
