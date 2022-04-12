package com.onedoorway.project.repository;

import com.onedoorway.project.model.ClientAllowances;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientAllowancesRepository extends JpaRepository<ClientAllowances, Long> {
    List<ClientAllowances> findByClient_IdAndDeleted(long id, Boolean deleted);
}
