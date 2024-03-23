package goorm.reinput.reminder.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import goorm.reinput.insight.domain.Insight;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Reminder extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    private Boolean isEnable;
    private LocalDateTime lastRemindedAt;

    @OneToOne(fetch = FetchType.LAZY)
    private Insight insight;

    @OneToOne(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReminderDate reminderDate;

    @OneToMany(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReminderQuestion> reminderQuestion;

    @Builder
    public Reminder(Boolean isEnable, LocalDateTime lastRemindedAt, Insight insight) {
        this.isEnable = isEnable;
        this.lastRemindedAt = lastRemindedAt;
        this.insight = insight;
    }
}
