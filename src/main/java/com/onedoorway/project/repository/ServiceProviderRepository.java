package com.onedoorway.project.repository;

import com.onedoorway.project.model.ServiceProvider;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    List<ServiceProvider> findByClient_IdAndDeleted(long id, Boolean deleted);
}
