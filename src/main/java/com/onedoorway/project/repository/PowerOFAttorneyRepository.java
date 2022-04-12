package com.onedoorway.project.repository;

import com.onedoorway.project.model.PowerOfAttorney;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerOFAttorneyRepository extends JpaRepository<PowerOfAttorney, Long> {
    List<PowerOfAttorney> findByClient_IdAndDeleted(long clientId, boolean deleted);
}
