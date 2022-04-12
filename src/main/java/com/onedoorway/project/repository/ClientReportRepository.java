package com.onedoorway.project.repository;

import com.onedoorway.project.model.ClientReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientReportRepository extends JpaRepository<ClientReport, Long> {
    Optional<ClientReport> findByLookup_IdAndClient_Id(long lookupId, long clientId);

    List<ClientReport> findAllByClient_Id(long id);
}
