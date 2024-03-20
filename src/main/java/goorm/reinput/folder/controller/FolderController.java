package goorm.reinput.folder.controller;

import goorm.reinput.folder.domain.dto.FolderCreateDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.folder.service.FolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
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

    @PostMapping
    public ResponseEntity<List<FolderResponseDto>> saveFolder(final @AuthenticationPrincipal Long userId, final @RequestBody FolderCreateDto folderCreateDto) {
        log.info("[FolderController] saveFolder {} called", userId);
        folderService.saveFolder(userId, folderCreateDto.getFolderName(), folderCreateDto.getFolderColor());
        return ResponseEntity.ok().body(folderService.getFolderList(userId));
    }

}
