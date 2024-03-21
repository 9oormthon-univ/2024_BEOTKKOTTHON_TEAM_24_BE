package goorm.reinput.reminder.repository;

import goorm.reinput.insight.domain.Insight;
import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReminderDateRepository extends JpaRepository<ReminderDate, Long> {
    Optional<ReminderDate> findByReminder(Reminder reminder);
}
