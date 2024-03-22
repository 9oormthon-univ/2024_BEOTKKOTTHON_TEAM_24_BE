package goorm.reinput.reminder.service;

import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderQuestion;
import goorm.reinput.reminder.domain.dto.ReminderInsightDto;
import goorm.reinput.reminder.domain.dto.ReminderInsightQueryDto;
import goorm.reinput.reminder.domain.dto.req.ReminderAnswerReqDto;
import goorm.reinput.reminder.domain.dto.ReminderQuestionDto;
import goorm.reinput.reminder.domain.dto.ReminderQuestionQueryDto;
import goorm.reinput.reminder.domain.dto.req.ReminderCalenderReqDto;
import goorm.reinput.reminder.domain.dto.res.ReminderAnswerResDto;
import goorm.reinput.reminder.domain.dto.res.ReminderCalenderResDto;
import goorm.reinput.reminder.domain.dto.res.ReminderQuestionResponseDto;
import goorm.reinput.reminder.repository.QuestionRepository;
import goorm.reinput.reminder.repository.ReminderQuestionRepository;
import goorm.reinput.reminder.repository.ReminderRepository;
import goorm.reinput.reminder.repository.impl.CustomReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final CustomReminderRepository customReminderRepository;
    private final QuestionRepository questionRepository;
    private final ReminderQuestionRepository reminderQuestionRepository;

    @Transactional
    public List<Long> makeReminderQuestionList(Long userId) {
        log.info("makeReminderQuestionList start");
        List<Reminder> reminders = customReminderRepository.findOldestReminders(userId);

        //오래된 리마인더중 reminderQuestion이 없는 경우 질문을 생성하고 있으면 그대로 가져온다.
        for (Reminder reminder : reminders) {
            if (reminder.getReminderQuestion() == null) {
                ReminderQuestion question = ReminderQuestion.builder()
                        .reminder(reminder)
                        .reminderQuestion(questionRepository.findRandomQuestion().getQuestion())
                        .build();
                reminderQuestionRepository.save(question);
            }
        }

        return reminders.stream().map(Reminder::getReminderId).toList();
    }
    public ReminderQuestionResponseDto getOlderReminder(Long userId){
        log.info("[ReminderService] getOlderReminder {}", userId);

        List<ReminderQuestionQueryDto> reminderQuestionQueryDtos = customReminderRepository.findOldestReminderDto(makeReminderQuestionList(userId));

        /*reminder reminderQuestion 의 update 시간이 하나라도 오늘이 아니면
        ReminderQuestionResponseDto todayClear false
         */
        boolean todayClear = reminderQuestionQueryDtos.stream()
                .anyMatch(dto -> dto.getReminderUpdatedAt().toLocalDate().isEqual(java.time.LocalDate.now()));

        return ReminderQuestionResponseDto.builder()
                .todayClear(todayClear)
                .reminderQuestionList(reminderQuestionQueryDtos.stream().map(dto ->
                                ReminderQuestionDto.builder()
                                        .reminderQuestion(dto.getReminderQuestion())
                                        .insightId(dto.getInsightId())
                                        .reminderId(dto.getReminderId())
                                        .insightTitle(dto.getInsightTitle())
                                        .insightMainImage(dto.getInsightMainImage())
                                        .insightTagList(dto.getInsightTagList())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

    }
    @Transactional
    public ReminderAnswerResDto answerReminderQuestion(Long userId, ReminderAnswerReqDto reminderAnswer){
        log.info("[ReminderService] getReminderAnswer userId: {}, reminderId: {}", userId, reminderAnswer.getReminderId());

        Reminder reminder = reminderRepository.findById(reminderAnswer.getReminderId()).orElseThrow(() -> new IllegalArgumentException("reminder not found"));
        ReminderQuestion reminderQuestion = reminder.getReminderQuestion();
        ReminderQuestion newQuestion = ReminderQuestion.builder()
                .reminder(reminder)
                .reminderQuestion(reminderQuestion.getReminderQuestion())
                .reminderAnswer(reminderAnswer.getReminderAnswer())
                .build();

        ReminderQuestion reminderQuestion1 = reminderQuestionRepository.save(newQuestion);

        return ReminderAnswerResDto.builder()
                .insightId(reminderQuestion1.getReminder().getInsight().getInsightId())
                .build();

    }

    public ReminderCalenderResDto getReminderCalender(Long userId, ReminderCalenderReqDto reminderCalenderReqDto){
        log.info("[ReminderService] getReminderCalender userId: {}", userId);
        //remind 할 remind id 조회
        List<Long> reminderIds = customReminderRepository.findRemindersToNotify(userId, reminderCalenderReqDto.getRequestDate());
        //reminde id로 Insight 조회
        List<ReminderInsightQueryDto> reminderInsightQueryDtos = customReminderRepository.findReminderInsights(reminderIds);

        // ReminderCalenderResDto로 변환
        return ReminderCalenderResDto.builder()
                .date(reminderCalenderReqDto.getRequestDate())
                .remindTotal(reminderInsightQueryDtos.size())
                .remindRead((int) reminderInsightQueryDtos.stream().filter(ReminderInsightQueryDto::isTodayRead).count())
                .remindInsightList(reminderInsightQueryDtos.stream().map(dto ->
                        ReminderInsightDto.builder()
                                .insightId(dto.getInsightId())
                                .insightTitle(dto.getInsightTitle())
                                .insightMainImage(dto.getInsightMainImage())
                                .insightTagList(dto.getInsightTagList())
                                .todayRead(dto.isTodayRead())
                                .build())
                        .collect(Collectors.toList())).build();
    }
}
