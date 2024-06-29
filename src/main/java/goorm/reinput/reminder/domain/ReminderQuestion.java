package goorm.reinput.reminder.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import goorm.reinput.global.domain.BaseTimeEntity;
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
public class ReminderQuestion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderQuestionId;

    private String reminderQuestion;
    private String reminderAnswer;
    private Long questionId;
    private LocalDateTime answeredAt;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Reminder reminder;

    @Builder
    public ReminderQuestion(String reminderQuestion, String reminderAnswer, Long questionId, LocalDateTime answeredAt, Reminder reminder) {
        this.reminderQuestion = reminderQuestion;
        this.reminderAnswer = reminderAnswer;
        this.questionId = questionId;
        this.answeredAt = answeredAt;
        this.reminder = reminder;
    }
}
