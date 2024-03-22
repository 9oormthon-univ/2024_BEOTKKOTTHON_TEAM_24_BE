package goorm.reinput.reminder.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.reminder.domain.RemindType;
import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderQuestion;
import goorm.reinput.reminder.domain.dto.ReminderQuestionQueryDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static goorm.reinput.insight.domain.QHashTag.hashTag;
import static goorm.reinput.insight.domain.QInsight.insight;
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
    //가장 읽은 오래된 리마인더 5개만 조회, insight와 reminder question join
    public List<ReminderQuestionQueryDto> findOldestReminderDto(List<Long> reminderIds){
        if(reminderIds.isEmpty()){
            return Collections.emptyList();
        }
        List<ReminderQuestionQueryDto> results = queryFactory
                .select(Projections.fields(ReminderQuestionQueryDto.class,
                        reminder.reminderQuestion.reminderQuestion.as("reminderQuestion"),
                        insight.insightId.as("insightId"),
                        reminder.reminderId.as("reminderId"),
                        insight.insightTitle.as("insightTitle"),
                        insight.insightMainImage.as("insightMainImage"),
                        reminder.lastRemindedAt.as("lastRemindedAt")
                ))
                .from(reminder)
                .join(reminder.insight, insight)
                .where(reminder.reminderId.in(reminderIds))
                .orderBy(reminder.lastRemindedAt.asc())
                .fetch();
        results.forEach(dto -> {
            List<String> tags = queryFactory
                    .select(hashTag.hashTagName)
                    .from(hashTag)
                    .where(hashTag.insight.insightId.eq(dto.getInsightId()))
                    .fetch();
            dto.setInsightTagList(tags);
        });

        return results;

    }

    public List<Reminder> findOldestReminders(Long userId) {
        return queryFactory
                .selectFrom(reminder)
                .orderBy(reminder.lastRemindedAt.asc())
                .where(reminder.isEnable.isTrue().and(insight.folder.user.userId.eq(userId)))
                .join(reminder.insight, insight)
                .limit(5)
                .fetch();
    }

    // 리마인드할 인사이트 조회
    public List<Reminder> findRemindersToNotify(Long userId) {

        // 오늘 날짜와 요일
        LocalDate today = LocalDate.now();
        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        int todayMonthDay = today.getDayOfMonth();

        return queryFactory
                .selectFrom(reminder)
                .join(reminder.reminderDate, reminderDate)
                .join(reminder.insight, insight)
                .where(reminder.isEnable.isTrue()
                        .and(insight.folder.user.userId.eq(userId))
                        .and(
                                reminderDate.remindType.eq(RemindType.DEFAULT)
                                        .and(
                                                reminder.lastRemindedAt.after(LocalDate.now().minusDays(1).atStartOfDay())
                                                        .or(reminder.lastRemindedAt.after(LocalDate.now().minusWeeks(1).atStartOfDay()))
                                                        .or(reminder.lastRemindedAt.after(LocalDate.now().minusMonths(1).atStartOfDay()))
                                        )
                                        .or(
                                                reminderDate.remindType.eq(RemindType.WEEK)
                                                        .and(reminderDate.remindDays.contains(todayDayOfWeek.getValue()))
                                                        .and(insight.folder.user.userId.eq(userId))
                                        )
                                        .or(
                                                reminderDate.remindType.eq(RemindType.MONTH)
                                                        .and(reminderDate.remindDays.contains(todayMonthDay))
                                                        .and(insight.folder.user.userId.eq(userId))
                                        )
                        )
                ).fetch();

    }
}
