package goorm.reinput.reminder.repository;

import goorm.reinput.reminder.domain.ReminderQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderQuestionRepository extends JpaRepository<ReminderQuestion, Long> {

}
