package com.naarith.starter.features.user.mapper;

import com.naarith.starter.features.user.dto.UserDTO;
import com.naarith.starter.features.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    public User toUserModel(UserDTO userDTO) {
        return User.builder()
                .uid(userDTO.uid())
                .email(userDTO.email())
                .password(userDTO.password())
                .firstName(userDTO.firstName())
                .lastName(userDTO.lastName())
                .isCompetedProfile(userDTO.isCompetedProfile())
                .build();
    }

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .uid(user.getUid())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profileImagePath(user.getProfileImage() != null ? user.getProfileImage().getPath() : null)
                .isCompetedProfile(user.isCompetedProfile())
                .build();
    }
}
