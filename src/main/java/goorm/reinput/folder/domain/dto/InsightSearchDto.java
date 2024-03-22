package goorm.reinput.folder.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class InsightSearchDto {
    private Long insightId;
    private String insightMainImage;
    private String insightTitle;
    private String insightSummary;
    private String insightMemo;
    private List<String> hashTagList;
    // 검색 우선순위를 위한 필드들
    private transient int titleMatchScore;
    private transient int summaryMatchScore;
    private transient int tagMatchScore;
    private transient int memoMatchScore;

    @Builder
    public InsightSearchDto(Long insightId, String insightMainImage, String insightTitle, String insightSummary, String insightMemo, List<String> hashTagList) {
        this.insightId = insightId;
        this.insightMainImage = insightMainImage;
        this.insightTitle = insightTitle;
        this.insightSummary = insightSummary;
        this.insightMemo = insightMemo;
        this.hashTagList = hashTagList;
    }

    public void calculateMatchScores(String searchKeyword) {
        this.titleMatchScore = calculateMatchScore(searchKeyword, this.insightTitle);
        this.summaryMatchScore = calculateMatchScore(searchKeyword, this.insightSummary);
        this.tagMatchScore = this.hashTagList.contains(searchKeyword) ? 1 : 0;
        this.memoMatchScore = calculateMatchScore(searchKeyword, this.insightMemo);
    }

    private int calculateMatchScore(String keyword, String target) {
        return target != null && target.contains(keyword) ? 1 : 0;
    }
}
