package com.onedoorway.project.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentReviewDTO {
    private Long id;
    @NotNull private long incidentId;
    private Boolean factorHouseDesign;
    private Boolean factorHouseEquipment;
    private Boolean factorHouseFumes;
    private Boolean factorHouseGuards;
    private Boolean factorHouseOther;
    private Boolean factorHouseUnsuitable;
    private String factorHouseDescription;
    private String factorHouseWhatHappened;
    private String factorPersonName;
    private Boolean factorPersonAlcohol;
    private String reviewedBy;
    private Boolean factorPersonComplacency;
    private Boolean factorPersonLineOfFire;
    private Boolean factorPersonFrustration;
    private Boolean factorPersonLackTraining;
    private Boolean factorPersonLossBalance;
    private Boolean factorPersonManualHandling;
    private Boolean factorPersonMedicalCondition;
    private Boolean factorPersonNoPPE;
    private Boolean factorPersonOther;
    private Boolean factorPersonRushing;
    private Boolean factorPersonThreatening;
    private Boolean factorPersonFatigue;
    private Boolean factorPersonUnsafePractice;
    private Boolean factorPersonWorkingAlone;
    private String factorPersonDescription;
    private String factorPersonWhatHappened;
    private Boolean factorEnvAnimals;
    private Boolean factorEnvConfine;
    private Boolean factorEnvNight;
    private Boolean factorEnvOdours;
    private Boolean factorEnvOther;
    private Boolean factorEnvObstructions;
    private Boolean factorEnvRaining;
    private Boolean factorEnvSunGlare;
    private Boolean factorEnvSurface;
    private Boolean factorEnvTemperature;
    private Boolean factorEnvVegetation;
    private Boolean factorEnvHeight;
    private String factorEnvDescription;
    private String factorEnvWhatHappened;
    private String factorWorkDescription;
    private String factorWorkWhatHappened;
    @NotNull private Integer riskRating;
    @NotBlank private String consequences;
    @NotBlank private String likelihood;
    @NotBlank private String correctiveAction;
    @NotBlank private String dueDate;
    @NotBlank private String actionsToImplement;
    @NotBlank private String allocatedTo;
    private String supportPlanUpdate;
    private String codeBreach;
    private String furtherSupport;
    private String howPrevented;
    private String followUp;
}
