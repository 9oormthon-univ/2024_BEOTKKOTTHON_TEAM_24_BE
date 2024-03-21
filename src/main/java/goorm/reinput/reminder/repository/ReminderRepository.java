package goorm.reinput.reminder.repository;

import goorm.reinput.insight.domain.Insight;
import goorm.reinput.reminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    Optional<Reminder> findByInsight(Insight insight);
}
