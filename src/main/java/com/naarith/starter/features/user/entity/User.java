package com.naarith.starter.features.user.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.naarith.starter.entity.BaseEntity;
import com.naarith.starter.features.file.entity.File;
import jakarta.persistence.*;
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
    @PrePersist
    public void generateId() {
        if (uid == null) {
            uid = NanoIdUtils.randomNanoId();
        }
    }

    private String email;
    @Column(name = "password_hash")
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "is_completed_profile")
    private boolean isCompletedProfile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private File profileImage;
}
