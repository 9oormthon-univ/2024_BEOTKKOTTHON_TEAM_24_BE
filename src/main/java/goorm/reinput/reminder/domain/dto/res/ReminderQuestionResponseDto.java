package goorm.reinput.reminder.domain.dto.res;

import goorm.reinput.reminder.domain.dto.ReminderQuestionDto;
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
