package com.naarith.starter.features.user.service;

import com.naarith.starter.exception.ResourceNotFoundException;
import com.naarith.starter.features.auth.security.UserPrincipal;
import com.naarith.starter.features.file.entity.File;
import com.naarith.starter.features.file.enums.UsageType;
import com.naarith.starter.features.file.service.FileService;
import com.naarith.starter.features.user.dto.UserDTO;
import com.naarith.starter.features.user.dto.UserUpdateReqDTO;
import com.naarith.starter.features.user.exception.EmailAlreadyExistsException;
import com.naarith.starter.features.user.exception.InvalidProfileImageException;
import com.naarith.starter.features.user.exception.UserNotFoundException;
import com.naarith.starter.features.user.mapper.UserMapper;
import com.naarith.starter.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final FileService fileService;

    /**
     * Save a user to database.
     * @param userDTO user to be saved.
     * @throws EmailAlreadyExistsException in case the email is already in use.
     */
    public void createUser(UserDTO userDTO) throws EmailAlreadyExistsException {
        /// Check if the email is already in use.
        if (repository.findByEmail(userDTO.email()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        /// Save user to database
        repository.save(mapper.toUserModel(userDTO));
        log.info("User with email={} created successfully", userDTO.email());
    }

    /**
     * Get user by email
     * @param email user email
     * @return UserDTO
     * @throws UserNotFoundException in case the user not found
     */
    public UserDTO getUserByEmail(String email) throws UserNotFoundException {
        var user = repository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return mapper.toUserDTO(user);
    }

    /**
     * Update user information
     * @param reqDTO Request object
     * @param userPrincipal Current user
     * @throws ResourceNotFoundException in case profileImageId not exists or already in use
     */
    public void update(UserUpdateReqDTO reqDTO, UserPrincipal userPrincipal) throws ResourceNotFoundException {
        /// Get user
        var user = repository.findByEmail(userPrincipal.getUsername()).orElseThrow(UserNotFoundException::new);

        /// Update information
        user.setFirstName(reqDTO.firstName());
        user.setLastName(reqDTO.lastName());

        /// Update profile image if it changes
        if (reqDTO.profileImageId() != null && !reqDTO.profileImageId().equals(user.getProfileImage() != null ? user.getProfileImage().getId() : null)) {

            try {
                /// Move file from tmp folder to avt folder
                fileService.moveFromTmp(reqDTO.profileImageId(), UsageType.USER_AVATAR);
            } catch (ResourceNotFoundException e) {
                log.warn("User with email={} could not be moved", userPrincipal.getUsername());
                throw new InvalidProfileImageException(e.getMessage());
            }

            /// Delete old image if exists
            if (user.getProfileImage() != null) {
                fileService.deleteFile(user.getProfileImage().getId());
            }

            /// Update user profile image
            user.setProfileImage(File.builder().id(reqDTO.profileImageId()).build());

        }

        /// Update setup status
        user.setCompetedProfile(true);

        /// Save update
        repository.save(user);
        log.info("User with email={} updated successfully", user.getEmail());
    }
}
