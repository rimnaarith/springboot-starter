package com.naarith.starter.features.user.repository;

import com.naarith.starter.features.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(attributePaths = {"profileImage"})
    Optional<User> findByEmail(String email);
}
