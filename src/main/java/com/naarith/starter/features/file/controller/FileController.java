package com.naarith.starter.features.file.controller;

import com.naarith.starter.features.auth.security.UserPrincipal;
import com.naarith.starter.features.file.dto.FileUploadResDTO;
import com.naarith.starter.features.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class FileController {
    private final FileService service;

    /// =========================
    /// Upload file
    /// =========================
    @Operation(summary = "Upload a file")
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation error.")
    })
    @PostMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResDTO> upload(@RequestParam MultipartFile file, @AuthenticationPrincipal UserPrincipal user) {
        log.info("Received request to upload fileSize={}, type={}, user={}", file.getSize(), file.getContentType(),user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(file, user));
    }

    /// =========================
    /// Serve file
    /// =========================

}
