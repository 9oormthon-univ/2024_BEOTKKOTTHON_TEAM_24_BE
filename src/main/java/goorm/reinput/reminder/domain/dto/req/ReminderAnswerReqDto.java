package goorm.reinput.reminder.domain.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderAnswerReqDto {
    private Long reminderId;
    private String reminderQuestion;
    private String reminderAnswer;

    @Builder
    public ReminderAnswerReqDto(Long reminderId, String reminderQuestion, String reminderAnswer) {
        this.reminderId = reminderId;
        this.reminderQuestion = reminderQuestion;
        this.reminderAnswer = reminderAnswer;
    }
}
