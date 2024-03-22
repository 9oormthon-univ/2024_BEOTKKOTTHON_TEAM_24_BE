package goorm.reinput.reminder.service;

import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderQuestion;
import goorm.reinput.reminder.domain.dto.ReminderQuestionQueryDto;
import goorm.reinput.reminder.domain.dto.ReminderQuestionResponseDto;
import goorm.reinput.reminder.repository.QuestionRepository;
import goorm.reinput.reminder.repository.ReminderQuestionRepository;
import goorm.reinput.reminder.repository.ReminderRepository;
import goorm.reinput.reminder.repository.impl.CustomReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final CustomReminderRepository customReminderRepository;
    private final QuestionRepository questionRepository;
    private final ReminderQuestionRepository reminderQuestionRepository;

    private List<Long> makeReminderQuestionList(Long userId) {
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
//    private ReminderQuestionResponseDto getOlderReminder(Long userId){
//        log.info("[ReminderService] getOlderReminder {}", userId);
//
//        List<ReminderQuestionQueryDto> reminderQuestionQueryDtos = customReminderRepository.findOldestReminderDto(makeReminderQuestionList(userId));
//
//
//
//        //reminder reminderQuestion 의 update 시간이 오늘이 아니면 false
//
//    }

}
