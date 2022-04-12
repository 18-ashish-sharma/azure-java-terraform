package com.onedoorway.project.model;

import java.time.Instant;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
@Table(name = "house_contacts")
public class HouseContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_contact_id")
    private long id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private NoticeStatus status;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "house_contacts_houses",
            joinColumns = @JoinColumn(name = "house_contact_id"),
            inverseJoinColumns = @JoinColumn(name = "house_id"))
    private Set<House> houses;

    @Column(name = "caption")
    private String caption;

    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone1")
    private String phone1;

    @Column(name = "phone2")
    private String phone2;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @Column(name = "notes")
    private String notes;

    @Column(name = "last_Updated_By")
    private String lastUpdatedBy;

    @Column(name = " last_updated_at")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant lastUpdatedAt;
}
