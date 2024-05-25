package goorm.reinput.reminder.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ReminderDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderDateId;

    @JsonBackReference
    @OneToOne
    private Reminder reminder;

    @Enumerated(EnumType.STRING)
    private RemindType remindType;

    @ElementCollection
    @CollectionTable(name = "remind_days", joinColumns = @JoinColumn(name = "reminder_id"))
    @Column(name = "remind_day")
    private List<Integer> remindDays;


    @Builder
    public ReminderDate(Reminder reminder, RemindType remindType, List<Integer> remindDays){
        this.reminder = reminder;
        this.remindType = remindType;
        this.remindDays = remindDays;
    }
}
