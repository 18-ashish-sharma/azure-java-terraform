package com.onedoorway.project.repository;

import com.onedoorway.project.model.Incident;
import com.onedoorway.project.model.RaisedFor;
import com.onedoorway.project.model.Status;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Incident getById(long id);

    List<Incident> findAllByHouse_HouseCodeAndClient_Name(
            String houseCode, String name, Pageable pageable);

    List<Incident> findAllByHouse_HouseCode(String houseCode, Pageable pageable);

    List<Incident> findAllByClient_Name(String name, Pageable pageable);

    List<Incident> findAllByReportedById(Long id, Pageable pageable);

    List<Incident> findAllByRaisedForAndCreatedAtBetweenAndStatusNot(
            RaisedFor raisedFor, Instant startDate, Instant endDate, Status status);
}
