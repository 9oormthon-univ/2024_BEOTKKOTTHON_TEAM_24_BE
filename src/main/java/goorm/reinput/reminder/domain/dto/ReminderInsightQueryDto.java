package goorm.reinput.reminder.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ReminderInsightQueryDto {
    private Long insightId;
    private String insightTitle;
    private String insightMainImage;
    private LocalDateTime lastRemindedAt;
    private List<String> insightTagList;
    private boolean todayRead;

    @Builder
    public ReminderInsightQueryDto(Long insightId, String insightTitle, String insightMainImage, List<String> insightTagList, boolean todayRead) {
        this.insightId = insightId;
        this.insightTitle = insightTitle;
        this.insightMainImage = insightMainImage;
        this.insightTagList = insightTagList;
        this.todayRead = todayRead;
    }

}
