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
        name = "client_transport",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"client_id", "od_car", "car_registration"})
        })
public class ClientTransport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_transport_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = " od_car")
    @Enumerated(EnumType.ORDINAL)
    private YesNo odCar;

    @Column(name = "car_registration")
    private String carRegistration;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "cappedKMs", columnDefinition = "TEXT")
    private String cappedKMs;

    @Column(name = "car_reg_expiry")
    private LocalDate carRegExpiry;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "car_make_year")
    private String carMakeYear;

    @Column(name = " is_travel_protocol")
    @Enumerated(EnumType.ORDINAL)
    private YesNo isTravelProtocol;

    @Column(name = "travel_protocol")
    private String travelProtocol;

    @Column(name = "comprehensive_insurance")
    @Enumerated(EnumType.ORDINAL)
    private YesNo comprehensiveInsurance;

    @Column(name = "insurance_policy_number")
    private String insurancePolicyNumber;

    @Column(name = "authorised_person")
    private String authorisedPerson;

    @Column(name = "authorised_person_contactNumber")
    private String authorisedPersonContactNumber;

    @Column(name = "roadside_assistance_covered")
    @Enumerated(EnumType.ORDINAL)
    private YesNo roadSideAssistanceCovered;

    @Column(name = "insurance_agency")
    private String insuranceAgency;

    @Column(name = "insurance_contact_number")
    private String insuranceContactNumber;

    @Column(name = "last_uploaded_by")
    private String lastUploadedBy;
}
