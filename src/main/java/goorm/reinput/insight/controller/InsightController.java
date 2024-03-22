package goorm.reinput.insight.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.insight.domain.dto.InsightCreateDto;
import goorm.reinput.insight.domain.dto.InsightModifyDto;
import goorm.reinput.insight.domain.dto.InsightResponseDto;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
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

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/insight")
public class InsightController {

    private final InsightService insightService;
    private final UserService userService;

    @Operation(summary = "인사이트 저장", description = "유저가 인사이트를 등록할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @PostMapping()
    public void saveInsight(@Parameter(hidden = true) final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody InsightCreateDto insightCreateDto) {
        log.info("[InsightController] saveInsight {} called", principalDetails.getUserId());
        insightService.saveInsight(principalDetails.getUserId(), insightCreateDto);
    }

    @Operation(summary = "인사이트 상세보기", description = "유저가 인사이트의 상세정보를 확인할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/{insightId}")
    public ResponseEntity<InsightResponseDto> getInsightDetail(@Parameter(hidden = true) final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long insightId) {
        log.info("[InsightController] getInsightDetail {} called", principalDetails.getUserId());

        // 인사이트 리스트 반환
        return ResponseEntity.ok().body(insightService.getInsightDetail(principalDetails.getUserId(), insightId));
    }

    @Operation(summary = "폴더 내 인사이트 리스트 가져오기", description = "유저가 폴더 클릭 시 폴더 내 인사이트 리스트를 가져옵니다")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<InsightSimpleResponseDto>> getInsightList(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long folderId) {
        Long userId =  principalDetails.getUserId();
        log.info("[InsightController] getInsightList userId = {}, folderId = {} called", userId, folderId);
        return ResponseEntity.ok().body(insightService.getInsightList(userId, folderId));
    }

    @Operation(summary = "인사이트 수정", description = "유저가 인사이트를 수정할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @PutMapping()
    public void modifyInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody InsightModifyDto insightModifyDto) {
        Long userId =  principalDetails.getUserId();
        log.info("[InsightController] modifyInsight {} called", userId);
        insightService.modifyInsight(userId, insightModifyDto);
    }

    @Operation(summary = "인사이트 삭제", description = "유저가 인사이트를 삭제할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @DeleteMapping("/{insightId}")
    public Boolean deleteInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long insightId) {
        Long userId =  principalDetails.getUserId();
        log.info("[InsightController] deleteInsight userId = {}, insightId = {} called", userId, insightId);
        return insightService.deleteInsight(insightId);
    }
}
