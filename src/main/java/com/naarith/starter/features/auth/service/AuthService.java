package com.naarith.starter.features.auth.service;

import com.naarith.starter.features.auth.dto.LoginReqDTO;
import com.naarith.starter.features.auth.dto.LoginResDTO;
import com.naarith.starter.features.auth.dto.RegisterReqDTO;
import com.naarith.starter.features.auth.exception.InvalidCredentialsExceptions;
import com.naarith.starter.features.auth.exception.TokenInvalidException;
import com.naarith.starter.features.auth.security.CustomUserDetailsService;
import com.naarith.starter.features.auth.security.UserPrincipal;
import com.naarith.starter.features.auth.service.model.LoginResult;
import com.naarith.starter.features.user.dto.UserDTO;
import com.naarith.starter.features.user.exception.EmailAlreadyExistsException;
import com.naarith.starter.features.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Register new user
     * @param reqDTO Request object
     */
    public void register(RegisterReqDTO reqDTO) {

        /// Convert request to userDTO
        var userDTO = UserDTO.builder()
                .email(reqDTO.email())

                /// Encode password
                .password(passwordEncoder.encode(reqDTO.password()))
                .build();

        try {
            userService.createUser(userDTO);
            log.info("Successfully registered with email={}", reqDTO.email());
        } catch (EmailAlreadyExistsException e) {
            log.warn("Registration failed. Email {} already exists.", reqDTO.email());
            throw e;
        }
    }

    /**
     * Login a user with email and password
     * @param reqDTO Request object
     * @return Response DTO with generated refresh token
     */
    public LoginResult login(LoginReqDTO reqDTO) {
        final UserPrincipal userPrincipal;
        try {
            /// Authenticate a user's credentials
            var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(reqDTO.email(), reqDTO.password()));
            userPrincipal = (UserPrincipal) auth.getPrincipal();
            log.info("Successfully authenticated for email={}", reqDTO.email());
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.warn("Login failed for email={}. Invalid credentials", reqDTO.email());
            /// Invalid email/password or User not found
            throw new InvalidCredentialsExceptions();
        }

        /// Generate tokens
        var accessToken = jwtService.generateAccessToken(userPrincipal);
        var refreshToken = jwtService.generateRefreshToken(userPrincipal);
        return LoginResult.builder()
                .resDTO(LoginResDTO
                        .builder()
                        .email(userPrincipal.userDTO().email())
                        .accessToken(accessToken)
                        .isCompletedProfile(userPrincipal.userDTO().isCompletedProfile())
                        .build()
                )
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Refresh user token
     * @param refreshToken User generated refresh token
     * @return Response DTO with generated refresh token
     */
    public LoginResult refreshToken(String refreshToken) {
        /// Validate refresh token
        /// TokenExpiredException and TokenInvalidException are handle in GlobalExceptionHandler
        var tokenUser = jwtService.validateRefreshToken(refreshToken);
        log.info("Refresh token attempt for email={}", tokenUser.getUsername());

        /// Check if the user still exists.
        final UserPrincipal userPrincipal;
        try {
            userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(tokenUser.getUsername());
        } catch (UsernameNotFoundException e) {
            log.warn("User with email={} does not exist.", tokenUser.getUsername());
            throw new TokenInvalidException();
        }

        /// Generate tokens
        var newAccessToken = jwtService.generateAccessToken(userPrincipal);
        var newRefreshToken = jwtService.generateRefreshToken(userPrincipal);

        log.info("Successfully refresh the token for the email={}", userPrincipal.getUsername());
        return LoginResult.builder()
                .resDTO(LoginResDTO
                        .builder()
                        .email(userPrincipal.userDTO().email())
                        .accessToken(newAccessToken)
                        .isCompletedProfile(userPrincipal.userDTO().isCompletedProfile())
                        .build()
                )
                .refreshToken(newRefreshToken)
                .build();

    }


}
