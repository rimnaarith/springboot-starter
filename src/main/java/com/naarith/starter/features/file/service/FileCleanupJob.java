package com.naarith.starter.features.file.service;

import com.naarith.starter.features.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupJob {
    private final FileRepository repository;
    private final StorageService storageService;

    /**
     * The unused file cleanup task runs every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupUnusedFiles() {
        log.info("Cleaning up unused files");
        var cutoff = Instant.now().minus(Duration.ofHours(24));
        var files = repository.findUnusedOlderThan(cutoff);

        for (var file : files) {
            storageService.delete(file.getPath());
            repository.delete(file);
        }
        log.info("{} files were deleted", files.size());
    }
}
