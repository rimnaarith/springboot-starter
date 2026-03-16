package com.naarith.starter.features.user.entity;

import com.naarith.starter.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    /// System generated id
    @Id
    @Column(updatable = false)
    private String uid;

    private String email;
    @Column(name = "password_hash")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "is_completed_profile")
    private boolean isCompetedProfile;
    @Column(name = "profile_image_path")
    private String profileImagePath;
}
