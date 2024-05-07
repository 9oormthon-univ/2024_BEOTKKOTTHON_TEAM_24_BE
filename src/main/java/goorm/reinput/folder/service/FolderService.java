package goorm.reinput.folder.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.domain.dto.*;
import goorm.reinput.folder.repository.CustomFolderRepository;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.global.util.AESUtil;
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
    private final CustomInsightRepository customInsightRepository;

    public List<FolderResponseDto> getFolderList(Long userId) {
        log.info("[FolderService] getFolderList {} called", userId);
        if(userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
        }

        return folderRepository.getFolderList(userId).orElseThrow(() -> {
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
            folderName = "null folder";
           // throw new IllegalArgumentException("folderName is null");
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

        folderRepository.updateFolder(userId, folderDto);
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
    public void copyFolder(Long userId, String token) {
        // 토큰 해독
        String decryptedString = AESUtil.decrypt(token);
        String[] parts = decryptedString.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Decrypted data format is incorrect.");
        }

        Long folderId = Long.parseLong(parts[0]);
        boolean isCopyable = Boolean.parseBoolean(parts[1]);
        log.info("[FolderService] copyFolder {} called", userId);
        if (userId == null) {
            log.error("[FolderService] userId is null");
            throw new IllegalArgumentException("userId is null");
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
        if(folderRepository.findByFolderIdAndUser(folderShareDto.getFolderId(), userRepository.findByUserId(userId).orElseThrow()).isEmpty()) {
            log.error("[FolderService] folder not found with id {}", folderShareDto.getFolderId());
            throw new IllegalArgumentException("folder not found with id " + folderShareDto.getFolderId());
        }

        String toEncrypt = folderShareDto.getFolderId() + "@" + folderShareDto.isCopyable();
        String encryptedString = AESUtil.encrypt(toEncrypt);

        String baseURL = "https://reinput.info";

        return FolderShareResponseDto.builder()
                .url(String.format("%s/insight/share?token=%s", baseURL, encryptedString))
                .build();
    }


    public List<InsightSimpleResponseDto> findAllInsights(Long userId, String keyword) {
        log.info("[FolderService] findAllInsights {} called", userId);
        // todo : searchInsights 쿼리 최적화 버전 테스트후 적용
        List<InsightSearchDto> insightSearchDtos = folderRepository.searchInsight(folderRepository.searchInsightInFolder(userId, keyword));

        // Comparator를 정의하여 제목, 요약, 태그, 메모 순으로 정렬
        insightSearchDtos.forEach(insightSearchDto -> insightSearchDto.calculateMatchScores(keyword));

        List<InsightSearchDto> sortedInsightSearchDtos = insightSearchDtos.stream()
                .sorted(Comparator.comparing(InsightSearchDto::getMatchScore).reversed())
                .toList();


        return sortedInsightSearchDtos.stream().map(insightSearchDto -> InsightSimpleResponseDto.builder()
                .insightId(insightSearchDto.getInsightId())
                .insightMainImage(insightSearchDto.getInsightMainImage())
                .insightTitle(insightSearchDto.getInsightTitle())
                .insightSummary(insightSearchDto.getInsightSummary())
                .insightTagList(insightSearchDto.getInsightTagList())
                .build())
                .collect(Collectors.toList());
    }

}
