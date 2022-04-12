package com.onedoorway.project.repository;

import com.onedoorway.project.model.ClientNDISPlan;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientNDISPlanRepository extends JpaRepository<ClientNDISPlan, Long> {
    ClientNDISPlan findByClient_IdAndStartDateAndEndDateAndFundingTypeAndLevel(
            long id, LocalDate startDate, LocalDate endDate, String fundingType, String level);

    List<ClientNDISPlan> findByClientId(long clientId);
}
