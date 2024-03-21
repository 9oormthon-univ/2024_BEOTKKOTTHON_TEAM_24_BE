package goorm.reinput.insight.domain.dto;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.InsightImage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 인사이트 저장 dto
@Getter
@NoArgsConstructor
public class InsightRequestDto {

    private String insightUrl;
    private String insightTitle;
    private String insightSummary;
    private String insightMainImage;
    private String insightMemo;
    private String insightSource;
    private Integer viewCount;
    private List<HashTag> hashTagList;
    private List<InsightImage> insightImageList;
    private String folderName;
}
