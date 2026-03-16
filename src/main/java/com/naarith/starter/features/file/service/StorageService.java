package com.naarith.starter.features.file.service;

import com.naarith.starter.features.file.exception.StorageException;
import com.naarith.starter.features.file.service.model.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    /**
     * Save file
     * @param file file to be saved
     * @return StoredFile
     * @throws StorageException Storage save error
     */
    StoredFile store(MultipartFile file) throws StorageException;
    InputStream load(String path);

    /**
     * Delete file
     * @param path file path
     */
    void delete(String path);
    boolean exists(String path);
}
