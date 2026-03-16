package com.naarith.starter.features.auth.security;

import com.naarith.starter.features.user.dto.UserDTO;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public record UserPrincipal(UserDTO userDTO) implements UserDetails {
    public static UserPrincipal of(String uid, String email) {
        return new UserPrincipal(UserDTO.builder()
                .uid(uid)
                .email(email)
                .build()
        );
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public @Nullable String getPassword() {
        return userDTO.password();
    }

    @Override
    public @NonNull String getUsername() {
        return userDTO.email();
    }
}
