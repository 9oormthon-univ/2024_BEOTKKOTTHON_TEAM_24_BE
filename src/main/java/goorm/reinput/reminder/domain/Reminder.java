package goorm.reinput.reminder.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import goorm.reinput.insight.domain.Insight;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Reminder extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    private Boolean isEnable;
    private LocalDateTime lastRemindedAt;

    @OneToOne(fetch = FetchType.LAZY)
    private Insight insight;

    @OneToOne(mappedBy = "reminder")
    private ReminderDate reminderDate;

    @OneToOne(mappedBy = "reminder")
    private ReminderQuestion reminderQuestion;

    @Builder
    public Reminder(Boolean isEnable, LocalDateTime lastRemindedAt, Insight insight) {
        this.isEnable = isEnable;
        this.lastRemindedAt = lastRemindedAt;
        this.insight = insight;
    }
}
