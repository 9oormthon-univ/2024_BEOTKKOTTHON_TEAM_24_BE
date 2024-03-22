package goorm.reinput.reminder.repository;

import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReminderQuestionRepository extends JpaRepository<ReminderQuestion, Long> {
    Optional<List<ReminderQuestion>> findByReminder(Reminder reminder);
}
