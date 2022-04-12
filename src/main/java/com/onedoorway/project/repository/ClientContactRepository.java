package com.onedoorway.project.repository;

import com.onedoorway.project.model.ClientContact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientContactRepository extends JpaRepository<ClientContact, Long> {
    List<ClientContact> findAllByClient_Id(long id);
}
