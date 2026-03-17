package com.naarith.starter.features.file.mapper;

import com.naarith.starter.features.file.dto.FileUploadResDTO;
import com.naarith.starter.features.file.entity.File;
import com.naarith.starter.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileMapper {
    public FileUploadResDTO toFileUploadResDTO(File file) {
        return FileUploadResDTO.builder()
                .fileId(file.getId())
                .url(Utils.makeUploadUrl(file.getPath()))
                .build();
    }
}
