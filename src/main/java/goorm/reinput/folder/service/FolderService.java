package goorm.reinput.folder.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.folder.domain.dto.FolderShareDto;
import goorm.reinput.folder.domain.dto.FolderShareResponseDto;
import goorm.reinput.folder.repository.CustomFolderRepository;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightImage;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
import goorm.reinput.insight.repository.CustomInsightRepository;
import goorm.reinput.insight.repository.HashTagRepository;
import goorm.reinput.insight.repository.InsightImageRepository;
import goorm.reinput.insight.repository.InsightRepository;
import goorm.reinput.user.domain.User;
import goorm.reinput.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final InsightRepository insightRepository;
    private final InsightImageRepository insightImageRepository;
    private final HashTagRepository hashTagRepository;
    private final CustomFolderRepository customFolderRepository;
    private final CustomInsightRepository customInsightRepository;

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
    public void updateFolder(Long userId, FolderDto folderDto){
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
    // userId 검색후 공유를 원하는 folder를 찾아서 copy
    @Transactional
    public void copyFolder(Long userId, Long folderId) {
        log.info("[FolderService] copyFolder {} called", userId);
        if (userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
        }
        if (folderId == null) {
            log.error("[FolderService] folderId is null");
            throw new IllegalArgumentException("folderId is null");
        }

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("folder not found with id " + folderId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Folder savedFolder = folderRepository.save(Folder.builder()
                .user(user)
                .folderName(folder.getFolderName())
                .folderColor(folder.getFolderColor())
                .build());

        customInsightRepository.findByInsightFolderId(folderId)
                .orElseThrow(() -> new IllegalArgumentException("insight not found with folderId " + folderId))
                .forEach(insight -> copyInsightWithDependencies(insight, savedFolder));
    }

    /*
    copy folder에 필요한 insight, insightImage, hashTag를 복사
     */

    private void copyInsightWithDependencies(Insight insight, Folder savedFolder) {
        Insight copiedInsight = insightRepository.save(Insight.builder()
                .folder(savedFolder)
                .insightTitle(insight.getInsightTitle())
                .insightUrl(insight.getInsightUrl())
                .insightSummary(insight.getInsightSummary())
                .insightMainImage(insight.getInsightMainImage())
                .insightMemo(insight.getInsightMemo())
                .insightSource(insight.getInsightSource())
                .viewCount(0)
                .build());

        hashTagRepository.findByInsight(insight).ifPresent(hashTags -> hashTags.forEach(hashTag ->
                hashTagRepository.save(HashTag.builder()
                        .insight(copiedInsight)
                        .hashTagName(hashTag.getHashTagName())
                        .build())
        ));

        insightImageRepository.findByInsight(insight).ifPresent(insightImages -> insightImages.forEach(insightImage ->
                insightImageRepository.save(InsightImage.builder()
                        .insight(copiedInsight)
                        .insightImageUrl(insightImage.getInsightImageUrl())
                        .build())
        ));
    }

    public FolderShareResponseDto createShareLink(Long userId, FolderShareDto folderShareDto) {
        log.info("[FolderService] createShareLink {} called", userId);
        /* folder가 userId에 속하는지 확인
         없으면 exception
         있으면 share link 생성*/
        if(!folderRepository.findByFolderIdAndUser(folderShareDto.getFolderId(), userRepository.findByUserId(userId).orElseThrow()).isPresent()) {
            log.error("[FolderService] folder not found with id {}", folderShareDto.getFolderId());
            throw new IllegalArgumentException("folder not found with id " + folderShareDto.getFolderId());
        }

        //todo : create share link
        return FolderShareResponseDto.builder()
                .url(String.format("http://reinput.online/folder/share/%d/%s", folderShareDto.getFolderId(), folderShareDto.isCopyable()))
                .build();
    }


    public List<InsightSimpleResponseDto> sortInsights(List<InsightSimpleResponseDto> insights) {
        //todo: insight로 책임 이동
        // Comparator를 정의하여 제목, 요약, 태그, 메모 순으로 정렬
        Comparator<InsightSimpleResponseDto> comparator = Comparator
                .comparingInt(InsightSimpleResponseDto::getTitlePriority)
                .thenComparingInt(InsightSimpleResponseDto::getSummaryPriority)
                .thenComparingInt(InsightSimpleResponseDto::getTagsPriority)
                .thenComparingInt(InsightSimpleResponseDto::getMemoPriority);

        // 정의된 Comparator를 사용하여 리스트 정렬
        return insights.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

}
