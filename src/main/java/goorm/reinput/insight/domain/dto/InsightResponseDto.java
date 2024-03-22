package goorm.reinput.insight.domain.dto;

import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.InsightImage;
import goorm.reinput.reminder.domain.RemindType;
import lombok.*;

import java.util.List;

// 인사이트 상세보기 dto
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InsightResponseDto {

    private Long insightId;
    private String insightTitle;
    private String insightUrl;
    private String insightSummary;
    private String insightMainImage;
    private String insightMemo;
    private String insightSource;
    private Integer viewCount;
    private List<String> hashTagList;
    private List<String> insightImageList;
    private boolean isEnable;
    private RemindType remindType;
    private List<Integer> remindDays;
    private String folderName;
    private Long folderId;
}
