package goorm.reinput.reminder.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.reminder.domain.dto.req.ReminderAnswerReqDto;
import goorm.reinput.reminder.domain.dto.res.ReminderAnswerResDto;
import goorm.reinput.reminder.domain.dto.res.ReminderQuestionResponseDto;
import goorm.reinput.reminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reminder")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/question")
    public ResponseEntity<ReminderQuestionResponseDto> getReminderQuestion(final @AuthenticationPrincipal PrincipalDetails principalDetails){
        log.info("[ReminderController] getReminderQuestion");
        return ResponseEntity.ok(reminderService.getOlderReminder(principalDetails.getUserId()));
    }

    @PostMapping("/answer")
    public ResponseEntity<ReminderAnswerResDto> answerReminderQuestion(final @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody ReminderAnswerReqDto reqDto){
        log.info("[ReminderController] answerReminderQuestion");
        return ResponseEntity.ok(reminderService.answerReminderQuestion(principalDetails.getUserId(), reqDto));
    }
}
