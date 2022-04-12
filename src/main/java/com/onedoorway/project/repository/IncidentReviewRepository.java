package com.onedoorway.project.repository;

import com.onedoorway.project.model.IncidentReview;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentReviewRepository extends JpaRepository<IncidentReview, Long> {
    List<IncidentReview> findByIncident_Id(long id);

    IncidentReview getByIncident_Id(long id);
}
