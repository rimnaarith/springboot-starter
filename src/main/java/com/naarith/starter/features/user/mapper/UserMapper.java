package com.naarith.starter.features.user.mapper;

import com.naarith.starter.features.user.dto.UserDTO;
import com.naarith.starter.features.user.dto.UserDetailsResDTO;
import com.naarith.starter.features.user.entity.User;
import com.naarith.starter.utils.Utils;
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
                .isCompletedProfile(userDTO.isCompletedProfile())
                .build();
    }

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .uid(user.getUid())
                .email(user.getEmail())
                .password(user.getPassword())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isCompletedProfile(user.isCompletedProfile())
                .build();
    }

    public UserDetailsResDTO toUserDetailsDTO(User user) {
        return UserDetailsResDTO.builder()
                .uid(user.getUid())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profileImage(user.getProfileImage() != null ? Utils.makeUploadUrl(user.getProfileImage().getPath()) : null)
                .isCompletedProfile(user.isCompletedProfile())
                .build();
    }
}
