package goorm.reinput.reminder.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.reminder.domain.RemindType;
import goorm.reinput.reminder.domain.Reminder;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static goorm.reinput.reminder.domain.QReminder.reminder;
import static goorm.reinput.reminder.domain.QReminderDate.reminderDate;


@Repository
@Slf4j
public class CustomReminderRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public CustomReminderRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(this.em);
    }
    //가장 읽은 오래된 리마인더 5개만 조회
    public List<Reminder> findOldestReminders() {
        return queryFactory
                .selectFrom(reminder)
                .orderBy(reminder.lastRemindedAt.asc())
                .where(reminder.isEnable.isTrue())
                .limit(5)
                .fetch();
    }

    // 리마인드할 인사이트 조회
    public List<Reminder> findRemindersToNotify() {

        // 오늘 날짜와 요일
        LocalDate today = LocalDate.now();
        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        int todayMonthDay = today.getDayOfMonth();

        return queryFactory
                .selectFrom(reminder)
                .join(reminder.reminderDate, reminderDate)
                .where(reminder.isEnable.isTrue()
                        .and(reminderDate.remindType.eq(RemindType.DEFAULT)
                                .and(reminder.lastRemindedAt.after(LocalDate.now().minusDays(1).atStartOfDay())
                                        .or(reminder.lastRemindedAt.after(LocalDate.now().minusWeeks(1).atStartOfDay()))
                                        .or(reminder.lastRemindedAt.after(LocalDate.now().minusMonths(1).atStartOfDay()))))
                        .or(reminderDate.remindType.eq(RemindType.WEEK)
                                .and(reminderDate.remindDays.contains(todayDayOfWeek.getValue())))
                        .or(reminderDate.remindType.eq(RemindType.MONTH)
                                .and(reminderDate.remindDays.contains(todayMonthDay))))
                .fetch();
    }
}
