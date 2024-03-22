package goorm.reinput.reminder.domain.dto.req;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReminderCalenderReqDto {
    private LocalDate requestDate;

    @Builder
    public ReminderCalenderReqDto(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
}
