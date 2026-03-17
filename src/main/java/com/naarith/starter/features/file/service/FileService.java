package com.naarith.starter.features.file.service;

import com.naarith.starter.exception.ResourceNotFoundException;
import com.naarith.starter.features.auth.security.UserPrincipal;
import com.naarith.starter.features.file.dto.FileUploadResDTO;
import com.naarith.starter.features.file.entity.File;
import com.naarith.starter.features.file.enums.UsageType;
import com.naarith.starter.features.file.exception.StorageException;
import com.naarith.starter.features.file.repository.FileRepository;
import com.naarith.starter.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository repository;
    private final FileValidator fileValidator;
    private final StorageService storageService;

    /**
     * Handle file upload
     * @param file Upload file
     * @param user User who upload the file
     * @return FileUploadResDTO
     */
    public FileUploadResDTO upload(MultipartFile file, UserPrincipal user) {

        fileValidator.validateImage(file);

        var stored = storageService.store(file);
        var entity = File.builder()
                .path(stored.path())
                .size(stored.size())
                .originalName(stored.originalName())
                .name(stored.uniqueName())
                .mimeType(stored.contentType())
                .user(User.builder()
                        .uid(user.userDTO().uid())
                        .build())
                .usageType(UsageType.TEMP)
                .build();
        repository.save(entity);
        log.info("File {} has been uploaded", stored.path());
        return FileUploadResDTO.builder()
                .fileId(entity.getId())
                .build();
    }

    /**
     * Move files from tmp folder to another folder based on its usage
     * @param fileId The id of file to be moved
     * @param usageType Usage type
     * @throws ResourceNotFoundException in case of not found or already in use
     */
    public void moveFromTmp(String fileId, UsageType usageType) throws ResourceNotFoundException {
        var file = repository.findById(fileId).orElseThrow(() -> new ResourceNotFoundException("File with id " + fileId + " not found"));
        if (!file.getUsageType().equals(UsageType.TEMP)) {
            log.warn("File with id {} id already in use", fileId);
            throw new ResourceNotFoundException("File with id " + fileId + " already in use");
        }

        var newPath = storageService.moveFromTmp(file.getName(), usageType);

        file.setUsageType(usageType);
        file.setPath(newPath);
        repository.save(file);
        log.info("File information updated successfully");
    }

    /**
     * Delete a file
     * @param fileId The id of file to be deleted
     */
    public void deleteFile(String fileId) {
        storageService.delete(fileId);
        repository.deleteById(fileId);
        log.info("File with id {} deleted successfully", fileId);
    }
}
