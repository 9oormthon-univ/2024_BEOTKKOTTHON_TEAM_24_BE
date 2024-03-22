package goorm.reinput.reminder.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ReminderQuestion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderQuestionId;

    private String reminderQuestion;
    private String reminderAnswer;

    @OneToOne(fetch = FetchType.LAZY)
    private Reminder reminder;

    @Builder
    public ReminderQuestion(String reminderQuestion, String reminderAnswer, Reminder reminder) {
        this.reminderQuestion = reminderQuestion;
        this.reminderAnswer = reminderAnswer;
        this.reminder = reminder;
    }
}
