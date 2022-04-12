package com.onedoorway.project.repository;

import com.onedoorway.project.model.ClientDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDocumentRepository extends JpaRepository<ClientDocument, Long> {
    Optional<ClientDocument> findByClient_IdAndFolder_IdAndDocName(
            long clientId, long folderId, String docName);

    List<ClientDocument> findAllByClient_IdAndFolder_Id(long clientId, long folderId);
}
