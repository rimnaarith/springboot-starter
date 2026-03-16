package com.naarith.starter.features.file.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.naarith.starter.entity.BaseEntity;
import com.naarith.starter.features.file.enums.UsageType;
import com.naarith.starter.features.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "files")
public class File extends BaseEntity {
    @Id
    private String id;
    @PrePersist
    public void generateId() {
        if (id == null) {
            id = NanoIdUtils.randomNanoId();
        }
    }
    private String name;
    private String path;
    @Column(name = "mime_type")
    private String mimeType;
    private long size;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type")
    private UsageType usageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false, referencedColumnName = "uid")
    private User user;

}
