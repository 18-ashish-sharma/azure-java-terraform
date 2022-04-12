package com.onedoorway.project.repository;

import com.onedoorway.project.model.Lookup;
import com.onedoorway.project.model.LookupType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LookupRepository extends JpaRepository<Lookup, Long> {
    List<Lookup> findAllByLookupType(LookupType lookupType);
}
