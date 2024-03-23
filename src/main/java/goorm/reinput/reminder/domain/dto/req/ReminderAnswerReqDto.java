package goorm.reinput.reminder.domain.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderAnswerReqDto {
    private Long reminderQuestionId;
    private String reminderQuestion;
    private String reminderAnswer;

    @Builder
    public ReminderAnswerReqDto(Long reminderQuestionId, String reminderQuestion, String reminderAnswer) {
        this.reminderQuestionId = reminderQuestionId;
        this.reminderQuestion = reminderQuestion;
        this.reminderAnswer = reminderAnswer;
    }
}
