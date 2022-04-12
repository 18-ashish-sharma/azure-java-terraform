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
@Entity(name = "incident_reviews")
public class IncidentReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private long id;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "incident_id", referencedColumnName = "incident_id")
    private Incident incident;

    @Column(name = "factor_house_design")
    private Boolean factorHouseDesign;

    @Column(name = "factor_house_equipment")
    private Boolean factorHouseEquipment;

    @Column(name = "factor_house_fumes")
    private Boolean factorHouseFumes;

    @Column(name = "factor_house_guards")
    private Boolean factorHouseGuards;

    @Column(name = "factor_house_other")
    private Boolean factorHouseOther;

    @Column(name = "factor_house_unsuitable")
    private Boolean factorHouseUnsuitable;

    @Column(name = "factor_house_description")
    private String factorHouseDescription;

    @Column(name = "factor_house_what_happened")
    private String factorHouseWhatHappened;

    @Column(name = "factor_person_name")
    private String factorPersonName;

    @Column(name = "factor_person_alcohol")
    private Boolean factorPersonAlcohol;

    @Column(name = "factor_person_complacency")
    private Boolean factorPersonComplacency;

    @Column(name = "factor_person_line_of_fire")
    private Boolean factorPersonLineOfFire;

    @Column(name = "factor_person_frustration")
    private Boolean factorPersonFrustration;

    @Column(name = "factor_person_lack_training")
    private Boolean factorPersonLackTraining;

    @Column(name = "factor_person_loss_balance")
    private Boolean factorPersonLossBalance;

    @Column(name = "factor_person_manual_handling")
    private Boolean factorPersonManualHandling;

    @Column(name = "factor_person_medical_condition")
    private Boolean factorPersonMedicalCondition;

    @Column(name = "factor_person_no_ppe")
    private Boolean factorPersonNoPPE;

    @Column(name = "factor_person_other")
    private Boolean factorPersonOther;

    @Column(name = "factor_person_rushing")
    private Boolean factorPersonRushing;

    @Column(name = "factor_person_threatening")
    private Boolean factorPersonThreatening;

    @Column(name = "factor_person_fatigue")
    private Boolean factorPersonFatigue;

    @Column(name = "factor_person_unsafe_practice")
    private Boolean factorPersonUnsafePractice;

    @Column(name = "factor_person_working_alone")
    private Boolean factorPersonWorkingAlone;

    @Column(name = "factor_person_description")
    private String factorPersonDescription;

    @Column(name = "factor_person_what_happened")
    private String factorPersonWhatHappened;

    @Column(name = "factor_env_animals")
    private Boolean factorEnvAnimals;

    @Column(name = "factor_env_confine")
    private Boolean factorEnvConfine;

    @Column(name = "factor_env_night")
    private Boolean factorEnvNight;

    @Column(name = "factor_env_odours")
    private Boolean factorEnvOdours;

    @Column(name = "factor_env_other")
    private Boolean factorEnvOther;

    @Column(name = "factor_env_obstructions")
    private Boolean factorEnvObstructions;

    @Column(name = "factor_env_raining")
    private Boolean factorEnvRaining;

    @Column(name = "factor_env_sun_glare")
    private Boolean factorEnvSunGlare;

    @Column(name = "factor_env_surface")
    private Boolean factorEnvSurface;

    @Column(name = "factor_env_temperature")
    private Boolean factorEnvTemperature;

    @Column(name = "factor_env_vegetation")
    private Boolean factorEnvVegetation;

    @Column(name = "factor_env_height")
    private Boolean factorEnvHeight;

    @Column(name = "factor_env_description")
    private String factorEnvDescription;

    @Column(name = "factor_env_what_happened")
    private String factorEnvWhatHappened;

    @Column(name = "factor_work_description")
    private String factorWorkDescription;

    @Column(name = "factor_work_what_happened")
    private String factorWorkWhatHappened;

    @Column(name = "risk_rating")
    private Integer riskRating;

    @Column(name = "consequences")
    private String consequences;

    @Column(name = "likelihood")
    private String likelihood;

    @Column(name = "corrective_action")
    private String correctiveAction;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "actions_to_implement", columnDefinition = "TEXT")
    private String actionsToImplement;

    @Column(name = "allocated_to")
    private String allocatedTo;

    @Column(name = "how_prevented", columnDefinition = "TEXT")
    private String howPrevented;

    @Column(name = "follow_up", columnDefinition = "TEXT")
    private String followUp;

    @Column(name = "supportPlan_update")
    @Enumerated(EnumType.ORDINAL)
    private YesNo supportPlanUpdate;

    @Column(name = "code_breach")
    @Enumerated(EnumType.ORDINAL)
    private YesNo codeBreach;

    @Column(name = "further_support")
    @Enumerated(EnumType.ORDINAL)
    private YesNo furtherSupport;
}
