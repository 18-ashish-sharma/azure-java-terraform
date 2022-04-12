package com.onedoorway.project.repository;

import com.onedoorway.project.model.EmergencyPlan;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmergencyPlanRepository extends JpaRepository<EmergencyPlan, Long> {
    Optional<EmergencyPlan> findByHouseCode(String houseCode);
}
