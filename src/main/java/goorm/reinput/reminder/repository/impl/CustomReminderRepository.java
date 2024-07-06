package goorm.reinput.reminder.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.reminder.domain.RemindType;
import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.dto.ReminderInsightQueryDto;
import goorm.reinput.reminder.domain.dto.ReminderQuestionQueryDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static goorm.reinput.folder.domain.QFolder.folder;
import static goorm.reinput.insight.domain.QHashTag.hashTag;
import static goorm.reinput.insight.domain.QInsight.insight;
import static goorm.reinput.reminder.domain.QReminder.reminder;
import static goorm.reinput.reminder.domain.QReminderDate.reminderDate;
import static goorm.reinput.user.domain.QUser.user;
import static goorm.reinput.reminder.domain.QReminderQuestion.reminderQuestion1;


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
    public List<ReminderQuestionQueryDto> findOldestReminderDto(List<Long> reminderQuestionIds){
        if(reminderQuestionIds.isEmpty()){
            return Collections.emptyList();
        }

        List<ReminderQuestionQueryDto> results = queryFactory
                .select(Projections.fields(ReminderQuestionQueryDto.class,
                        reminderQuestion1.reminderQuestion.as("reminderQuestion"),
                        reminderQuestion1.updatedAt.as("reminderUpdatedAt"),
                        insight.insightId.as("insightId"),
                        reminder.reminderId.as("reminderId"),
                        reminderQuestion1.reminderQuestionId.as("reminderQuestionId"),
                        insight.insightTitle.as("insightTitle"),
                        insight.insightMainImage.as("insightMainImage"),
                        reminder.lastRemindedAt.as("lastRemindedAt"),
                        reminderQuestion1.answeredAt.as("answeredAt")
                ))
                .from(reminderQuestion1)
                .join(reminderQuestion1.reminder, reminder)
                .join(reminder.insight, insight)
                .where(reminderQuestion1.reminderQuestionId.in(reminderQuestionIds))
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
                .join(reminder.insight, insight)
                .join(insight.folder, folder)
                .join(folder.user, user)
                .where(user.userId.eq(userId)
                        .and(reminder.isEnable.isTrue()))
                .orderBy(reminder.lastRemindedAt.asc())
                .limit(5)
                .fetch();
    }

    public List<Long> findRemindersToNotifyV2(Long userId, LocalDate date) {
        DayOfWeek todayDayOfWeek = date.getDayOfWeek();
        int todayMonthDay = date.getDayOfMonth();

        return queryFactory
                .select(reminder.reminderId)
                .from(reminder)
                .join(reminder.reminderDate, reminderDate)
                .join(reminder.insight, insight)
                .where(reminder.isEnable.isTrue()
                        .and(insight.folder.user.userId.eq(userId))
                        .and(insight.createdAt.before(date.atStartOfDay()))  // 인사이트 추가 날짜 필터링
                        .and(
                                reminderDate.remindType.eq(RemindType.DEFAULT)
                                        .and(
                                                reminder.lastRemindedAt.eq(date.minusDays(1).atStartOfDay())
                                                        .or(reminder.lastRemindedAt.eq(date.minusWeeks(1).atStartOfDay()))
                                                        .or(reminder.lastRemindedAt.eq(date.minusMonths(1).atStartOfDay()))
                                        )
                                        .or(
                                                reminderDate.remindType.eq(RemindType.WEEK)
                                                        .and(reminderDate.remindDays.contains(todayDayOfWeek.getValue()))
                                                        .and(reminder.lastRemindedAt.before(date.atStartOfDay()))
                                        )
                                        .or(
                                                reminderDate.remindType.eq(RemindType.MONTH)
                                                        .and(reminderDate.remindDays.contains(todayMonthDay))
                                                        .and(reminder.lastRemindedAt.before(date.atStartOfDay()))
                                        )
                        )
                ).fetch();
    }

    public List<ReminderInsightQueryDto> findReminderInsights(List<Long> reminderIds, LocalDate reqDate){
        if(reminderIds.isEmpty()){
            return Collections.emptyList();
        }
        List<ReminderInsightQueryDto> results = queryFactory
                .select(Projections.fields(ReminderInsightQueryDto.class,
                        insight.insightId.as("insightId"),
                        insight.insightTitle.as("insightTitle"),
                        insight.insightMainImage.as("insightMainImage"),
                        insight.insightSummary.as("insightSummary"),
                        reminder.lastRemindedAt.as("lastRemindedAt")
                ))
                .from(reminder)
                .join(reminder.insight, insight)
                .where(reminder.reminderId.in(reminderIds))
                .fetch();
        results.forEach(dto -> {
            List<String> tags = queryFactory
                    .select(hashTag.hashTagName)
                    .from(hashTag)
                    .where(hashTag.insight.insightId.eq(dto.getInsightId()))
                    .fetch();
            dto.setInsightTagList(tags);
            dto.setTodayRead(Optional.ofNullable(dto.getLastRemindedAt())
                    .map(LocalDateTime::toLocalDate)
                    .map(date -> date.isEqual(reqDate))
                    .orElse(false));
        });

        return results;
    }

    //해당 insight가 reminders to notify에 해당되는지 확인
    public boolean isInsightInRemindersToNotify(Long userId, Long insightId) {
        long count = Optional.ofNullable(queryFactory
                .select(reminder.count())
                .from(reminder)
                .join(reminder.insight, insight)
                .where(reminder.isEnable.isTrue()
                        .and(insight.folder.user.userId.eq(userId))
                        .and(insight.insightId.eq(insightId))
                        .and(
                                reminder.lastRemindedAt.after(LocalDate.now().minusDays(1).atStartOfDay())
                                        .or(reminder.lastRemindedAt.after(LocalDate.now().minusWeeks(1).atStartOfDay()))
                                        .or(reminder.lastRemindedAt.after(LocalDate.now().minusMonths(1).atStartOfDay()))
                        )
                        .or(
                                reminder.reminderDate.remindType.eq(RemindType.WEEK)
                                        .and(reminder.reminderDate.remindDays.contains(LocalDate.now().getDayOfWeek().getValue()))
                                        .and(insight.folder.user.userId.eq(userId))
                        )
                        .or(
                                reminder.reminderDate.remindType.eq(RemindType.MONTH)
                                        .and(reminder.reminderDate.remindDays.contains(LocalDate.now().getDayOfMonth()))
                                        .and(insight.folder.user.userId.eq(userId))
                        )
                )
                .fetchOne()).orElse(0L); // count 쿼리는 결과가 단일 숫자이므로 fetchOne() 사용

        return count > 0;
    }
    //reminder lastView 업데이트
    public void updateLastView(Long insightId){
        queryFactory
                .update(reminder)
                .set(reminder.lastRemindedAt, LocalDateTime.now())
                .where(reminder.insight.insightId.eq(insightId))
                .execute();
    }
}
