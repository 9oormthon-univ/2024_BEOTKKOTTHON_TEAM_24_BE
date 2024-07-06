package goorm.reinput.reminder.service;

import goorm.reinput.reminder.domain.Question;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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


    public List<Long> makeReminderQuestionList(Long userId) {
        log.info("makeReminderQuestionList start");
        List<Reminder> reminders = customReminderRepository.findOldestReminders(userId);
        List<ReminderQuestion> reminderQuestions = new ArrayList<>();

        for (Reminder reminder : reminders) {
            Question question = questionRepository.findRandomQuestion();
            ReminderQuestion reminderQuestion = ReminderQuestion.builder()
                    .reminder(reminder)
                    .reminderQuestion(question.getQuestion())
                    .questionId(question.getId())
                    .build();
            reminderQuestions.add(reminderQuestionRepository.save(reminderQuestion));
        }

        return reminderQuestions.stream().map(ReminderQuestion::getReminderQuestionId).collect(Collectors.toList());
    }
    @Transactional
    public ReminderQuestionResponseDto getOlderReminder(Long userId){
        log.info("[ReminderService] getOlderReminder {}", userId);

        List<ReminderQuestionQueryDto> reminderQuestionQueryDtos = customReminderRepository.findOldestReminderDto(makeReminderQuestionList(userId));

        /*reminder
        * 오늘 날짜에 모두 응답했는지 확인
         */

        //todo : 빈 리스트일때 ture 반환 오류 수정
        boolean todayClear = reminderQuestionQueryDtos.stream().allMatch(ReminderQuestionQueryDto -> ReminderQuestionQueryDto.getAnsweredAt() != null);

        return ReminderQuestionResponseDto.builder()
                .todayClear(todayClear)
                .reminderQuestionList(reminderQuestionQueryDtos.stream().map(dto ->
                                ReminderQuestionDto.builder()
                                        .reminderQuestion(dto.getReminderQuestion())
                                        .insightId(dto.getInsightId())
                                        .reminderId(dto.getReminderId())
                                        .reminderQuestionId(dto.getReminderQuestionId())
                                        .insightTitle(dto.getInsightTitle())
                                        .insightMainImage(dto.getInsightMainImage())
                                        .insightTagList(dto.getInsightTagList())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

    }

    @Transactional
    public ReminderAnswerResDto answerReminderQuestion(Long userId, ReminderAnswerReqDto reminderAnswer){
        log.info("[ReminderService] getReminderAnswer userId: {}, reminderId: {}", userId, reminderAnswer.getReminderQuestionId());
        //reminderQuestionId로 reminderQuestion 조회
        ReminderQuestion reminderQuestion = reminderQuestionRepository.findById(reminderAnswer.getReminderQuestionId()).orElseThrow(() -> new IllegalArgumentException("해당 리마인더 질문이 존재하지 않습니다."));
        //reminderQuestion에 답변 저장
        reminderQuestion.setReminderAnswer(reminderAnswer.getReminderAnswer());
        reminderQuestion.setAnsweredAt(LocalDateTime.now());
        reminderQuestionRepository.save(reminderQuestion);
        return ReminderAnswerResDto.builder()
                .insightId(reminderQuestion.getReminder().getInsight().getInsightId())
                .build();
    }

    public ReminderCalenderResDto getReminderCalender(Long userId, ReminderCalenderReqDto reminderCalenderReqDto){
        log.info("[ReminderService] getReminderCalender userId: {}", userId);
        //remind 할 remind id 조회
        List<Long> reminderIds = customReminderRepository.findRemindersToNotifyV2(userId, reminderCalenderReqDto.getRequestDate());
        //reminde id로 Insight 조회
        List<ReminderInsightQueryDto> reminderInsightQueryDtos = customReminderRepository.findReminderInsights(reminderIds, reminderCalenderReqDto.getRequestDate());

        // ReminderCalenderResDto로 변환
        return ReminderCalenderResDto.builder()
                .date(reminderCalenderReqDto.getRequestDate())
                .remindTotal(reminderInsightQueryDtos.size())
                .remindRead((int) reminderInsightQueryDtos.stream().filter(ReminderInsightQueryDto::isTodayRead).count())
                .remindInsightList(reminderInsightQueryDtos.stream().map(dto ->
                        ReminderInsightDto.builder()
                                .insightId(dto.getInsightId())
                                .insightTitle(dto.getInsightTitle())
                                .insightSummary(dto.getInsightSummary())
                                .insightMainImage(dto.getInsightMainImage())
                                .insightTagList(dto.getInsightTagList())
                                .todayRead(dto.isTodayRead())
                                .build())
                        .collect(Collectors.toList())).build();
    }
}
