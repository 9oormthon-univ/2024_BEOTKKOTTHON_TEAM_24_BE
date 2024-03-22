package goorm.reinput.reminder.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReminderQuestionDto {
    private String reminderQuestion;
    private Long insightId;
    private Long reminderId;
    private String insightTitle;
    private String insightMainImage;
    private List<String> insightTagList;

    @Builder
    public ReminderQuestionDto(String reminderQuestion, Long insightId, Long reminderId, String insightTitle, String insightMainImage, List<String> insightTagList) {
        this.reminderQuestion = reminderQuestion;
        this.insightId = insightId;
        this.reminderId = reminderId;
        this.insightTitle = insightTitle;
        this.insightMainImage = insightMainImage;
        this.insightTagList = insightTagList;
    }
}
