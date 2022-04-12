package com.onedoorway.project.repository;

import com.onedoorway.project.model.Folder;
import com.onedoorway.project.model.NoticeStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> getByStatusAndClientId(NoticeStatus status, long id);

    List<Folder> findAllByClient_Id(long id);
}
