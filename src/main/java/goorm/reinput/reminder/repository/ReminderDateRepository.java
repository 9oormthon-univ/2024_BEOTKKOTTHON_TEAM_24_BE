package goorm.reinput.reminder.repository;

import goorm.reinput.reminder.domain.ReminderDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderDateRepository extends JpaRepository<ReminderDate, Long> {
}
