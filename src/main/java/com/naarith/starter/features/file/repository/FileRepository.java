package com.naarith.starter.features.file.repository;

import com.naarith.starter.features.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    @Query("""
        SELECT f
        FROM File f
        WHERE f.usageType = 'TEMP'
        AND f.createdAt < :cutoff
    """)
    List<File> findUnusedOlderThan(@Param("cutoff") Instant cutoff);
}
