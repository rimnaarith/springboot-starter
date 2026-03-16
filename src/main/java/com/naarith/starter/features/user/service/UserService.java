package com.naarith.starter.features.user.service;

import com.naarith.starter.features.user.dto.UserDTO;
import com.naarith.starter.features.user.exception.EmailAlreadyExistsException;
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
}
