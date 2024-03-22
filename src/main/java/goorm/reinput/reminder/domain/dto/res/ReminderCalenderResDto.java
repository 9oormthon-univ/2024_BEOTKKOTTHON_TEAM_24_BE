package goorm.reinput.reminder.domain.dto.res;

import goorm.reinput.reminder.domain.dto.ReminderInsightDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ReminderCalenderResDto {
    private LocalDate date;
    private int remindRead;
    private int remindTotal;
    private List<ReminderInsightDto> reminderInsightList;

    @Builder
    public ReminderCalenderResDto(LocalDate date, int remindRead, int remindTotal, List<ReminderInsightDto> reminderInsightList) {
        this.date = date;
        this.remindRead = remindRead;
        this.remindTotal = remindTotal;
        this.reminderInsightList = reminderInsightList;
    }
}
