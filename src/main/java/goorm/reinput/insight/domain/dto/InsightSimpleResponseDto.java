package goorm.reinput.insight.domain.dto;

import goorm.reinput.folder.domain.FolderColor;
import lombok.*;

import java.util.List;

// 인사이트 추천 목록, 인사이트 전체 검색, 폴더 내 인사이트 리스트, 폴더 내 인사이트 태그 검색
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InsightSimpleResponseDto {
    private Long insightId;
    private String insightMainImage;
    private String insightTitle;
    private String insightSummary;
    private List<String> insightTagList;
    private FolderColor folderColor;
}
