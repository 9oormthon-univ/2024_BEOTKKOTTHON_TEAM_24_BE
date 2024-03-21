package goorm.reinput.folder.controller;

import goorm.reinput.folder.domain.dto.*;
import goorm.reinput.folder.service.FolderService;
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
    public ResponseEntity<List<FolderResponseDto>> getFolderList(final @AuthenticationPrincipal Long userId) {
        log.info("[FolderController] getFolderList {} called", userId);
        return ResponseEntity.ok().body(folderService.getFolderList(userId));
    }

    @GetMapping("/share")
    public ResponseEntity<FolderShareResponseDto> createShareLink(final @AuthenticationPrincipal Long userId, final @RequestBody FolderShareDto folderShareDto) {
        log.info("[FolderController] createShareLink {} called", userId);
        return ResponseEntity.ok().body(folderService.createShareLink(userId, folderShareDto));
    }

    @GetMapping("/share/copy/{folderId}")
    public ResponseEntity<String> copyFolder(final @AuthenticationPrincipal Long userId, final @PathVariable Long folderId) {
        log.info("[FolderController] copyFolder {} called", userId);
        folderService.copyFolder(userId, folderId);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }
    /*
    //todo : 인사이트 전체 검색
    @GetMapping("/search")
    public ResponseEntity<List<InsightResponseDto>> searchInsight(final @AuthenticationPrincipal Long userId, final @RequestParam String keyword) {
        log.info("[FolderController] searchInsight {} called", userId);
        return ResponseEntity.ok().body(folderService.searchInsight(userId, keyword));
    }*/

    @PostMapping
    public ResponseEntity<List<FolderResponseDto>> saveFolder(final @AuthenticationPrincipal Long userId, final @RequestBody FolderCreateDto folderCreateDto) {
        log.info("[FolderController] saveFolder {} called", userId);
        folderService.saveFolder(userId, folderCreateDto.getFolderName(), folderCreateDto.getFolderColor());
        return ResponseEntity.ok().body(folderService.getFolderList(userId));
    }

    @PatchMapping
    public ResponseEntity<String> updateFolder(final @AuthenticationPrincipal Long userId, final @RequestBody FolderDto folderDto) {
        log.info("[FolderController] updateFolder {} called", userId);
        folderService.updateFolder(userId, folderDto);
        return ResponseEntity.ok().body("success");
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(final @AuthenticationPrincipal Long userId, final @PathVariable Long folderId) {
        log.info("[FolderController] deleteFolder {} called", userId);
        folderService.deleteFolder(userId, folderId);
        return ResponseEntity.ok().body("success");
    }

}
