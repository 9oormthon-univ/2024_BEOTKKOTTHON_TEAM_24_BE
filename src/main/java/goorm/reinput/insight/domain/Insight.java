package goorm.reinput.insight.domain;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Insight extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insightId;

    private String insightTitle;
    private String insightUrl;
    private String insightSummary;
    private String insightMainImage;
    private String insightMemo;
    private String insightSource;
    private Integer viewCount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folderId")
    private Folder folder;

    @OneToMany(mappedBy = "insight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HashTag> hashTagList;

    @OneToMany(mappedBy = "insight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InsightImage> insightImageList;

    @Builder
    public Insight(String insightTitle, String insightUrl, String insightSummary, String insightMainImage, String insightMemo, String insightSource, Integer viewCount, Folder folder) {
        this.insightTitle = insightTitle;
        this.insightUrl = insightUrl;
        this.insightSummary = insightSummary;
        this.insightMainImage = insightMainImage;
        this.insightMemo = insightMemo;
        this.insightSource = insightSource;
        this.viewCount = viewCount;
        this.folder = folder;
    }

}
