package goorm.reinput.folder.controller;

import goorm.reinput.folder.domain.dto.*;
import goorm.reinput.folder.service.FolderService;
import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderController {
    private final FolderService folderService;

    @Operation(summary = "폴더 리스트 가져오기", description = "유저가 폴더 리스트를 가져올 때 사용하는 API")
    @GetMapping
    public ResponseEntity<List<FolderResponseDto>> getFolderList(final @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("[FolderController] getFolderList {} called", principalDetails.getUserId());
        return ResponseEntity.ok().body(folderService.getFolderList(principalDetails.getUserId()));
    }
    @Operation(summary = "폴더 공유", description = "유저가 폴더를 공유할 때 사용하는 API")
    @GetMapping("/share")
    public ResponseEntity<FolderShareResponseDto> createShareLink(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Long folderId, @RequestParam boolean copyable) {
        FolderShareDto folderShareDto = new FolderShareDto(folderId, copyable);
        log.info("[FolderController] createShareLink {} called", principalDetails.getUserId());
        return ResponseEntity.ok().body(folderService.createShareLink(principalDetails.getUserId(), folderShareDto));
    }
    @Operation(summary = "폴더 복사", description = "본인의 폴더로 다른 사람의 폴더를 복사하는 API")
    @GetMapping("/share/copy/{token}")
    public ResponseEntity<String> copyFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable String token) {
        log.info("[FolderController] copyFolder {} called", principalDetails.getUserId());
        folderService.copyFolder(principalDetails.getUserId(), token);

        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }


    @Operation(summary = "인사이트 검색", description = "유저가 전체 인사이트를 검색할 때 사용하는 API")
    @PostMapping("/search")
    public ResponseEntity<List<InsightSimpleResponseDto>> searchInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody SearchReqDto searchReqDto) {
        log.info("[FolderController] searchInsight {} called", principalDetails.getUserId());
        return ResponseEntity.ok().body(folderService.findAllInsights(principalDetails.getUserId(), searchReqDto.getSearch()));
    }
    @Operation(summary = "폴더 생성", description = "유저가 폴더를 생성할 때 사용하는 API")
    @PostMapping
    public ResponseEntity<List<FolderResponseDto>> saveFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @RequestBody FolderCreateDto folderCreateDto) {
        log.info("[FolderController] saveFolder {} called", principalDetails.getUserId());
        folderService.saveFolder(principalDetails.getUserId(), folderCreateDto.getFolderName(), folderCreateDto.getFolderColor());
        return ResponseEntity.ok().body(folderService.getFolderList(principalDetails.getUserId()));
    }
    @Operation(summary = "폴더 수정", description = "유저가 폴더를 수정할 때 사용하는 API")
    @PatchMapping
    public ResponseEntity<String> updateFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @Valid @RequestBody FolderDto folderDto) {
        log.info("[FolderController] updateFolder {} called", principalDetails.getUserId());
        folderService.updateFolder(principalDetails.getUserId(), folderDto);
        return ResponseEntity.ok().body("success");
    }
    @Operation(summary = "폴더 삭제", description = "유저가 폴더를 삭제할 때 사용하는 API")
    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long folderId) {
        log.info("[FolderController] deleteFolder {} called", principalDetails.getUserId());
        folderService.deleteFolder(principalDetails.getUserId(), folderId);
        return ResponseEntity.ok().body("success");
    }

}
