package goorm.reinput.folder.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.folder.repository.CustomFolderRepository;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final CustomFolderRepository customFolderRepository;

    public List<FolderResponseDto> getFolderList(Long userId) {
        log.info("[FolderService] getFolderList {} called", userId);
        if(userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
        }


        return customFolderRepository.getFolderList(userId).orElseThrow(() -> {
            log.error("[FolderService] getFolderList failed");
            //todo : exception handling
            return new IllegalArgumentException("getFolderList failed");
        });
    }

    @Transactional
    public void saveFolder(Long userId, String folderName, FolderColor folderColor){
        log.info("[FolderService] saveFolder {} called", userId);
        if(userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
        }
        if(folderName == null) {
            log.error("[FolderService] folderName is null");
            throw new IllegalArgumentException("folderName is null");
        }
        if(folderColor == null) {
            log.error("[FolderService] folderColor is null");
            throw new IllegalArgumentException("folderColor is null");
        }
        Folder folder = Folder.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> {
                    //todo : exception handling
                    log.error("[FolderService] user not found");
                    return new IllegalArgumentException("user not found");
                }))
                .folderName(folderName)
                .folderColor(folderColor)
                .build();

        folderRepository.save(folder);
    }

    @Transactional
    public void modifyFolder(Long userId, FolderDto folderDto){
        log.info("[FolderService] modifyFolder {} called", userId);

        if(userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
        }

        customFolderRepository.updateFolder(userId, folderDto);
    }

    @Transactional
    public void deleteFolder(Long userId, Long folderId){
        log.info("[FolderService] deleteFolder {} called", userId);
        if(userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
        }
        if(folderId == null) {
            log.error("[FolderService] folderId is null");
            throw new IllegalArgumentException("folderId is null");
        }
        folderRepository.deleteById(folderId);
    }
}
