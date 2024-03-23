package goorm.reinput.folder.controller;

import goorm.reinput.folder.domain.dto.*;
import goorm.reinput.folder.service.FolderService;
import goorm.reinput.global.auth.PrincipalDetails;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
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

    @GetMapping
    public ResponseEntity<List<FolderResponseDto>> getFolderList(final @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("[FolderController] getFolderList {} called", principalDetails.getUserId());
        return ResponseEntity.ok().body(folderService.getFolderList(principalDetails.getUserId()));
    }

    @GetMapping("/share")
    public ResponseEntity<FolderShareResponseDto> createShareLink(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Long folderId, @RequestParam boolean copyable) {
        FolderShareDto folderShareDto = new FolderShareDto(folderId, copyable);
        log.info("[FolderController] createShareLink {} called", principalDetails.getUserId());
        return ResponseEntity.ok().body(folderService.createShareLink(principalDetails.getUserId(), folderShareDto));
    }

    @GetMapping("/share/copy/{token}")
    public ResponseEntity<String> copyFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable String token) {
        log.info("[FolderController] copyFolder {} called", principalDetails.getUserId());
        folderService.copyFolder(principalDetails.getUserId(), token);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<InsightSimpleResponseDto>> searchInsight(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @RequestBody SearchReqDto searchReqDto) {
        log.info("[FolderController] searchInsight {} called", principalDetails.getUserId());
        return ResponseEntity.ok().body(folderService.findAllInsights(principalDetails.getUserId(), searchReqDto.getSearch()));
    }

    @PostMapping
    public ResponseEntity<List<FolderResponseDto>> saveFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @RequestBody FolderCreateDto folderCreateDto) {
        log.info("[FolderController] saveFolder {} called", principalDetails.getUserId());
        folderService.saveFolder(principalDetails.getUserId(), folderCreateDto.getFolderName(), folderCreateDto.getFolderColor());
        return ResponseEntity.ok().body(folderService.getFolderList(principalDetails.getUserId()));
    }

    @PatchMapping
    public ResponseEntity<String> updateFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @RequestBody FolderDto folderDto) {
        log.info("[FolderController] updateFolder {} called", principalDetails.getUserId());
        folderService.updateFolder(principalDetails.getUserId(), folderDto);
        return ResponseEntity.ok().body("success");
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(final @AuthenticationPrincipal PrincipalDetails principalDetails, final @PathVariable Long folderId) {
        log.info("[FolderController] deleteFolder {} called", principalDetails.getUserId());
        folderService.deleteFolder(principalDetails.getUserId(), folderId);
        return ResponseEntity.ok().body("success");
    }

}
