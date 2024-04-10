package goorm.reinput.folder.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class InsightSearchDto {
    private Long insightId;
    private String insightMainImage;
    private String insightTitle;
    private String insightSummary;
    private String insightMemo;
    private List<String> insightTagList;
    // 검색 우선순위를 위한 필드들
    private transient int matchScore;

    @Builder
    public InsightSearchDto(Long insightId, String insightMainImage, String insightTitle, String insightSummary, String insightMemo, List<String> insightTagList) {
        this.insightId = insightId;
        this.insightMainImage = insightMainImage;
        this.insightTitle = insightTitle;
        this.insightSummary = insightSummary;
        this.insightMemo = insightMemo;
        this.insightTagList = insightTagList;
    }

    public void calculateMatchScores(String searchKeyword) {
        String keywordLowerCase = searchKeyword.toLowerCase();
        // 초기 점수는 0점
        this.matchScore = 0;

        // 제목에 검색어가 포함되면 8점 추가
        if (this.insightTitle != null && this.insightTitle.toLowerCase().contains(keywordLowerCase)) {
            this.matchScore += 8;
        }

        // 요약에 검색어가 포함되면 4점 추가
        if (this.insightSummary != null && this.insightSummary.toLowerCase().contains(keywordLowerCase)) {
            this.matchScore += 4;
        }

        // 메모에 검색어가 포함되면 1점 추가
        if (this.insightMemo != null && this.insightMemo.toLowerCase().contains(keywordLowerCase)) {
            this.matchScore += 1;
        }

        // 태그 목록 중 하나라도 검색어를 포함하고 있으면 2점 추가
        if (this.insightTagList != null && this.insightTagList.stream().anyMatch(tag -> tag.toLowerCase().contains(keywordLowerCase))) {
            this.matchScore += 2;
        }
    }

}
