package com.naarith.starter.features.auth.security;

import com.naarith.starter.features.user.exception.UserNotFoundException;
import com.naarith.starter.features.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        try {
            var userDTO = userService.getUserByEmail(username);
            return new UserPrincipal(userDTO);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("User not found!");
        }
    }
}
