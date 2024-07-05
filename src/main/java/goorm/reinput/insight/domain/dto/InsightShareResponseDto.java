package goorm.reinput.insight.domain.dto;

import goorm.reinput.folder.domain.FolderColor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InsightShareResponseDto {

    private Long insightId;
    private String insightTitle;
    private String insightUrl;
    private String insightSummary;
    private String insightMainImage;
    private String insightMemo;
    private String insightSource;
    private Integer viewCount;
    private List<String> insightTagList;
    private List<String> insightImageList;

    private String folderName;
    private Long folderId;
    private FolderColor folderColor;

    private Boolean isCopyable;
}
