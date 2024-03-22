package goorm.reinput.reminder.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReminderQuestionResponseDto {
    private boolean todayClear;
    private List<ReminderQuestionDto> reminderQuestionList;

    @Builder
    public ReminderQuestionResponseDto(boolean todayClear, List<ReminderQuestionDto> reminderQuestionList) {
        this.todayClear = todayClear;
        this.reminderQuestionList = reminderQuestionList;
    }
}
