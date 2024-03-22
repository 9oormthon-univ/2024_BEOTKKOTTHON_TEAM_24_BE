package goorm.reinput.reminder.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReminderAnswerDto {
    private Long insightId;

    @Builder
    public ReminderAnswerDto(Long insightId) {
        this.insightId = insightId;
    }
}
