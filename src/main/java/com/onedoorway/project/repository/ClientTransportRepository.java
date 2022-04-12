package com.onedoorway.project.repository;

import com.onedoorway.project.model.ClientTransport;
import com.onedoorway.project.model.YesNo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientTransportRepository extends JpaRepository<ClientTransport, Long> {
    ClientTransport findByClient_IdAndOdCarAndCarRegistrationAndDeleted(
            long id, YesNo odCar, String CarRegistration, Boolean deleted);

    List<ClientTransport> findByClient_Id(long id);
}
