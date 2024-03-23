package goorm.reinput.user.domain;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "reinput_user")
public class User extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long userId;

    @Column(unique = true)
    private String userEmail;

    private String userName;

    private String userPassword;

    @Enumerated(EnumType.STRING)
    private Job job;

    private boolean isEnable;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> folders;
    /*
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Motivation motivation;
     */
    @Builder
    public User(String userEmail, String userName, String userPassword, Job job, boolean isEnable) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPassword = userPassword;
        this.job = job;
        this.isEnable = isEnable;
    }
}
