package com.naarith.starter.features.file.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.naarith.starter.features.file.config.FileProperties;
import com.naarith.starter.features.file.enums.UsageType;
import com.naarith.starter.features.file.exception.StorageException;
import com.naarith.starter.features.file.service.model.StoredFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@ConditionalOnProperty(name = "application.storage.type", havingValue = "local")
public class LocalStorageService implements StorageService {

    private final Path root;

    public LocalStorageService(FileProperties fileProperties) {
        this.root = Paths.get(fileProperties.getUploadDir());
    }

    @Override
    public StoredFile store(MultipartFile file) throws StorageException {
        try {

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }

            var fileName = NanoIdUtils.randomNanoId() + extension;
            var tmpDir = root.resolve("tmp");
            var filePath = tmpDir.resolve(fileName);

            /// Ensure target directory exists
            Files.createDirectories(tmpDir);

            /// Save file
            Files.copy(file.getInputStream(), filePath);
            log.info("File tmp/{} has been stored", fileName);

            return StoredFile.builder()
                    .path("/tmp/" + fileName)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .extension(extension)
                    .originalName(originalFilename)
                    .uniqueName(fileName)
                    .build();
        } catch (Exception e) {
            log.error("Failed to store file. message={}", e.getMessage(), e);
            throw new StorageException("Failed to store file");
        }
    }

    @Override
    public InputStream load(String path) {
        try {
            return Files.newInputStream(root.resolve(path));
        } catch (IOException e) {
            throw new StorageException("File not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(root.resolve(path));
        } catch (IOException ignored) {}
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(root.resolve(path));
    }

    @Override
    public String moveFromTmp(String fileName, UsageType usageType) throws StorageException {
        try {
            var source = root.resolve("tmp").resolve(fileName);
            var targetDir = root.resolve(getFolderByUsage(usageType));

            /// Ensure target directory exists
            Files.createDirectories(targetDir);

            var target = targetDir.resolve(fileName);

            /// Move file
            Files.move(source, target);
            log.info("File tmp/{} has been moved {}", fileName, getFolderByUsage(usageType) + "/" + fileName);

            return getFolderByUsage(usageType) + "/" + fileName;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new StorageException("Failed to move file from tmp", e);
        }
    }

    /**
     * Resolve folder name by usage type
     * @param usageType Usage type
     * @return Folder name
     */
    private String getFolderByUsage(UsageType usageType) {
        return switch (usageType) {
            case TEMP -> "tmp";
            case USER_AVATAR -> "avt";
        };
    }
}
