package goorm.reinput.reminder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ReminderDate {
    @Id
    @OneToOne
    private Reminder reminder;

    @Enumerated(EnumType.STRING)
    private RemindType remindType;

    @ElementCollection
    @CollectionTable(name = "remind_days", joinColumns = @JoinColumn(name = "reminder_id"))
    @Column(name = "remind_day")
    private List<Integer> remindDays;
}
