package goorm.reinput.reminder.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.reminder.domain.dto.req.ReminderAnswerReqDto;
import goorm.reinput.reminder.domain.dto.req.ReminderCalenderReqDto;
import goorm.reinput.reminder.domain.dto.res.ReminderAnswerResDto;
import goorm.reinput.reminder.domain.dto.res.ReminderCalenderResDto;
import goorm.reinput.reminder.domain.dto.res.ReminderQuestionResponseDto;
import goorm.reinput.reminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    @Operation(summary = "리마인더 질문 가져오기", description = "유저가 리마인더 질문을 가져올 때 사용하는 API")
    @GetMapping("/question")
    public ResponseEntity<ReminderQuestionResponseDto> getReminderQuestion(final @AuthenticationPrincipal PrincipalDetails principalDetails){
        log.info("[ReminderController] getReminderQuestion");
        return ResponseEntity.ok(reminderService.getOlderReminder(principalDetails.getUserId()));
    }
    @Operation(summary = "리마인더 질문 답변하기", description = "유저가 리마인더 질문에 답변할 때 사용하는 API")
    @PostMapping("/answer")
    public ResponseEntity<ReminderAnswerResDto> answerReminderQuestion(final @AuthenticationPrincipal PrincipalDetails principalDetails,final @Valid @RequestBody ReminderAnswerReqDto reqDto){
        log.info("[ReminderController] answerReminderQuestion");
        return ResponseEntity.ok(reminderService.answerReminderQuestion(principalDetails.getUserId(), reqDto));
    }
    @Operation(summary = "리마인더 캘린더 가져오기", description = "유저가 리마인더 캘린더를 가져올 때 사용하는 API")
    @PostMapping("/calendar")
    public ResponseEntity<ReminderCalenderResDto> getReminderCalendar(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody ReminderCalenderReqDto reqDto){
        log.info("[ReminderController] getReminderCalendar");
        return ResponseEntity.ok(reminderService.getReminderCalender(principalDetails.getUserId(), reqDto));
    }
}
