package goorm.reinput.insight.domain.dto;

import goorm.reinput.reminder.domain.RemindType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 인사이트 저장 dto
@Getter
@NoArgsConstructor
public class InsightCreateDto {

    private String insightUrl;
    private String insightTitle;
    private String insightSummary;
    private String insightMainImage;
    private String insightMemo;
    private String insightSource;
    private Integer viewCount;
    private List<String> hashTagList;
    private List<String> insightImageList;
    private boolean enable;
    private RemindType remindType;
    private List<Integer> remindDays;
    private String folderName;
}
