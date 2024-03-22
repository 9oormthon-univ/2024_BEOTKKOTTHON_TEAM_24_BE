package goorm.reinput.reminder.domain.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderAnswerResDto {
    private Long insightId;

    @Builder
    public ReminderAnswerResDto(Long insightId) {
        this.insightId = insightId;
    }
}
