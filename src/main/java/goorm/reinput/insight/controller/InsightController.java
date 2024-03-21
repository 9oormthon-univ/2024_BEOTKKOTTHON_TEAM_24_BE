package goorm.reinput.insight.controller;

import goorm.reinput.insight.domain.dto.InsightCreateDto;
import goorm.reinput.insight.service.InsightService;
import goorm.reinput.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/insight")
public class InsightController {
/*
    private final InsightService insightService;
    private final UserService userService;

    @Operation(summary = "인사이트 저장", description = "유저가 인사이트를 등록할 때 사용하는 API 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201"),
            @ApiResponse(responseCode = "401"),
            @ApiResponse(responseCode = "403"),
            @ApiResponse(responseCode = "500")
    })
    @PostMapping()
    public ResponseEntity<InsightCreateDto> saveInsight(@Parameter(hidden = true) final @AuthenticationPrincipal Long userId, final @Valid @RequestBody InsightCreateDto insightCreateDto) {
        log.info("[InsightController] storeInsight {} called", userId);
        insightService.saveInsight(userId, insightCreateDto);

        // 인사이트 리스트 반환
        return
    }*/

    // 2. 인사이트 상세보기 GET


    // 3. 인사이트 전체 검색 GET

}
