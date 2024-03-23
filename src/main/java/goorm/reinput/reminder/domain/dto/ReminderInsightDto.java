package goorm.reinput.reminder.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReminderInsightDto {
    private Long insightId;
    private String insightTitle;
    private String insightSummary;
    private String insightMainImage;
    private List<String> insightTagList;
    private boolean todayRead;

    @Builder
    public ReminderInsightDto(Long insightId, String insightTitle, String insightSummary, String insightMainImage, List<String> insightTagList, boolean todayRead) {
        this.insightId = insightId;
        this.insightTitle = insightTitle;
        this.insightSummary = insightSummary;
        this.insightMainImage = insightMainImage;
        this.insightTagList = insightTagList;
        this.todayRead = todayRead;
    }
}
