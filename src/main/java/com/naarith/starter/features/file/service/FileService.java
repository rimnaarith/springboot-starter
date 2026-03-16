package com.naarith.starter.features.file.service;

import com.naarith.starter.features.auth.security.UserPrincipal;
import com.naarith.starter.features.file.dto.FileUploadResDTO;
import com.naarith.starter.features.file.entity.File;
import com.naarith.starter.features.file.enums.UsageType;
import com.naarith.starter.features.file.repository.FileRepository;
import com.naarith.starter.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
                .name(stored.originalName())
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
}
