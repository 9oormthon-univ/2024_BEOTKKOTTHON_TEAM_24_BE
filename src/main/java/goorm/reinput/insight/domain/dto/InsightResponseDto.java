package goorm.reinput.insight.domain.dto;

import goorm.reinput.reminder.domain.RemindType;
import goorm.reinput.reminder.domain.ReminderQuestion;
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
    private List<String> insightTagList;
    private List<String> insightImageList;
    private boolean isEnable;
    private RemindType remindType;
    private List<Integer> remindDays;
    private String folderName;
    private Long folderId;

    // 소유자가 볼 경우 리마인더 질문이 보임
    private List<ReminderQuestion> reminderQuestionList;
}
