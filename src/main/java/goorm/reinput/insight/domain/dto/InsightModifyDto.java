package goorm.reinput.insight.domain.dto;

import goorm.reinput.reminder.domain.RemindType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 인사이트 수정
@Getter
@NoArgsConstructor
public class InsightModifyDto {

    private Long folderId;

    private Long insightId;
    private String insightUrl;
    private String insightTitle;
    private String insightSummary;
    private String insightMainImage;
    private String insightMemo;
    private String insightSource;

    private List<String> hashTagList;
    private List<String> insightImageList;

    private boolean enable;
    private RemindType remindType;
    private List<Integer> remindDays;

    private List<Long> reminderQuestionId;
    private List<String> reminderAnswer; // Id 순서대로
}
