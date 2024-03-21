package goorm.reinput.reminder.repository;

import goorm.reinput.reminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
}
