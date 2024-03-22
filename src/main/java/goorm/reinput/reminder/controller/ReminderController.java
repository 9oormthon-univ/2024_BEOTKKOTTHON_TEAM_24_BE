package goorm.reinput.reminder.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.reminder.domain.dto.ReminderAnswerReqDto;
import goorm.reinput.reminder.domain.dto.ReminderQuestionResponseDto;
import goorm.reinput.reminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
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
    public ResponseEntity<Void> answerReminderQuestion(final @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody ReminderAnswerReqDto reqDto){
        log.info("[ReminderController] answerReminderQuestion");
        reminderService.answerReminderQuestion(principalDetails.getUserId(), reqDto);
        return ResponseEntity.ok().build();
    }
}
