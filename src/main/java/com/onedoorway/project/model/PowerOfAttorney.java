package com.onedoorway.project.model;

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
@Table(name = "power_of_attorney")
public class PowerOfAttorney {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "power_of_attorney_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false)
    private Client client;

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "adress1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "post_code")
    @Pattern(regexp = "\\d{4}")
    private String postCode;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
}
