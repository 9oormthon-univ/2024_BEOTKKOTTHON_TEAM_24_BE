package goorm.reinput.reminder.domain.dto;

import goorm.reinput.insight.domain.HashTag;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ReminderQuestionQueryDto {
    private String reminderQuestion;
    private LocalDateTime reminderUpdatedAt;
    private Long insightId;
    private Long reminderId;
    private Long reminderQuestionId;
    private String insightTitle;
    private String insightMainImage;
    private LocalDateTime reminderAt;
    private LocalDateTime answeredAt;
    private List<String> insightTagList;

    @Builder
    public ReminderQuestionQueryDto(String reminderQuestion, LocalDateTime reminderUpdatedAt, Long insightId, Long reminderId, Long reminderQuestionId, String insightTitle, String insightMainImage, LocalDateTime reminderAt, LocalDateTime answeredAt, List<String> insightTagList) {
        this.reminderQuestion = reminderQuestion;
        this.reminderUpdatedAt = reminderUpdatedAt;
        this.insightId = insightId;
        this.reminderId = reminderId;
        this.reminderQuestionId = reminderQuestionId;
        this.insightTitle = insightTitle;
        this.insightMainImage = insightMainImage;
        this.reminderAt = reminderAt;
        this.answeredAt = answeredAt;
        this.insightTagList = insightTagList;
    }


}
