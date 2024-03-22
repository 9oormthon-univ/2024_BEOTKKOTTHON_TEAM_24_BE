package goorm.reinput.insight.controller;

import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.insight.domain.dto.*;
import goorm.reinput.insight.service.InsightService;
import goorm.reinput.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "인사이트 저장", description = "유저가 인사이트를 등록할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "201"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @PostMapping()
    public void saveInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody InsightCreateDto insightCreateDto) {
        log.info("[InsightController] saveInsight {} called", principalDetails.getUserId());
        insightService.saveInsight(principalDetails.getUserId(), insightCreateDto);
    }

    @Operation(summary = "인사이트 상세보기", description = "유저가 인사이트의 상세정보를 확인할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/{insightId}")
    public ResponseEntity<InsightResponseDto> getInsightDetail(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long insightId) {
        log.info("[InsightController] getInsightDetail {} called", principalDetails.getUserId());

        // 인사이트 리스트 반환
        return ResponseEntity.ok().body(insightService.getInsightDetail(principalDetails.getUserId(), insightId));
    }

    @Operation(summary = "폴더 내 인사이트 리스트 가져오기", description = "유저가 폴더 클릭 시 폴더 내 인사이트 리스트를 가져옵니다")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<InsightSimpleResponseDto>> getInsightList(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long folderId) {
        Long userId = principalDetails.getUserId();
        log.info("[InsightController] getInsightList userId = {}, folderId = {} called", userId, folderId);
        return ResponseEntity.ok().body(insightService.getInsightList(userId, folderId));
    }

    @Operation(summary = "인사이트 수정", description = "유저가 인사이트를 수정할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @PutMapping()
    public void modifyInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody InsightModifyDto insightModifyDto) {
        Long userId = principalDetails.getUserId();
        log.info("[InsightController] modifyInsight {} called", userId);
        insightService.modifyInsight(userId, insightModifyDto);
    }

    @Operation(summary = "인사이트 삭제", description = "유저가 인사이트를 삭제할 때 사용하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @DeleteMapping("/{insightId}")
    public ResponseEntity<Boolean> deleteInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long insightId) {
        Long userId = principalDetails.getUserId();
        log.info("[InsightController] deleteInsight userId = {}, insightId = {} called", userId, insightId);
        return ResponseEntity.ok().body(insightService.deleteInsight(insightId));

    }

    @Operation(summary = "폴더 내 인사이트 태그로 검색하기", description = "폴더 내에서 검색 시 해당 검색어가 포함된 태그를 가진 모든 인사이트를 반환합니다")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/search/{folderId}/{tag}")
    public ResponseEntity<List<InsightSimpleResponseDto>> getInsightList(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long folderId, final @PathVariable String tag) {
        Long userId = principalDetails.getUserId();
        log.info("[InsightController] getInsightList userId = {}, folderId = {}, tag = {} called", userId, folderId, tag);
        return ResponseEntity.ok().body(insightService.getInsightListByFolderAndTag(userId, folderId, tag));
    }

    @Operation(summary = "인사이트 링크 대표 이미지 제공", description = "인사이트 첫 등록 시, 링크를 입력하면 해당 링크의 대표 이미지를 반환하는 API")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/ogimage")
    public ResponseEntity<String> getMainImage(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @RequestParam("url") String url) {
        Long userId = principalDetails.getUserId();
        log.info("[InsightController] getMainImage {} called, url = {}", userId, url);
        return ResponseEntity.ok().body(insightService.getMainImage(userId, url));
    }

    @Operation(summary = "공유된 폴더 url 접속하기", description = "유저의 폴더 내 인사이트 리스트를 볼 수 있는 url에 접속합니다.")
    @ApiResponses({@ApiResponse(responseCode = "200"), @ApiResponse(responseCode = "401"), @ApiResponse(responseCode = "403"), @ApiResponse(responseCode = "500")})
    @GetMapping("/share")
    public ResponseEntity<List<InsightShareResponseDto>> accessSharedFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam("token") String token) {
        Long userId = principalDetails.getUserId();
        log.info("[InsightController] accessSharedFolder {} called", userId);
        return ResponseEntity.ok().body(insightService.accessSharedFolder(userId, token));
    }

}
