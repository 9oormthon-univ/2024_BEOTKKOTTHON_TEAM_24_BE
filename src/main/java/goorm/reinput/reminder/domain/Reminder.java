package goorm.reinput.reminder.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private LocalDateTime lastViewedAt;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    private Insight insight;

    @JsonManagedReference
    @OneToOne(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReminderDate reminderDate;

    @JsonManagedReference
    @OneToMany(mappedBy = "reminder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReminderQuestion> reminderQuestion;

    @Builder
    public Reminder(Boolean isEnable, LocalDateTime lastRemindedAt, LocalDateTime lastViewedAt, Insight insight, ReminderDate reminderDate, List<ReminderQuestion> reminderQuestion) {
        this.isEnable = isEnable;
        this.lastRemindedAt = lastRemindedAt;
        this.lastViewedAt = lastViewedAt;
        this.insight = insight;
        this.reminderDate = reminderDate;
        this.reminderQuestion = reminderQuestion;
    }
}
