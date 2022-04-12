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
@Entity(name = "emergency_plans")
public class EmergencyPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private long id;

    @Column(name = "house_code", unique = true)
    private String houseCode;

    @Column(name = "url_emergency_plan")
    private String urlEmergencyPlan;

    @Column(name = "url_emergency_handout")
    private String urlEmergencyHandout;
}
