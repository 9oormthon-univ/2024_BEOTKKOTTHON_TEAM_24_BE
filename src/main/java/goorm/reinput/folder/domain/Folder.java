package goorm.reinput.folder.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Folder extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long folderId;
    private String folderName;
    private String folderColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;


    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Insight> insightList;
    @Builder
    public Folder(String folderName, String folderColor, User user) {
        this.folderName = folderName;
        this.folderColor = folderColor;
        this.user = user;
    }

}
