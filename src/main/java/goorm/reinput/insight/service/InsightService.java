package goorm.reinput.insight.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.folder.service.FolderService;
import goorm.reinput.global.util.AESUtil;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightImage;
import goorm.reinput.insight.domain.InsightRecommend;
import goorm.reinput.insight.domain.dto.*;
import goorm.reinput.insight.repository.CustomInsightRepository;
import goorm.reinput.insight.repository.HashTagRepository;
import goorm.reinput.insight.repository.InsightImageRepository;
import goorm.reinput.insight.repository.InsightRepository;
import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderDate;
import goorm.reinput.reminder.domain.ReminderQuestion;
import goorm.reinput.reminder.repository.ReminderDateRepository;
import goorm.reinput.reminder.repository.ReminderQuestionRepository;
import goorm.reinput.reminder.repository.ReminderRepository;
import goorm.reinput.reminder.repository.impl.CustomReminderRepository;
import goorm.reinput.user.domain.Job;
import goorm.reinput.user.domain.User;
import goorm.reinput.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsightService {
    private final InsightRepository insightRepository;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final FolderService folderService;
    private final ReminderDateRepository reminderDateRepository;
    private final ReminderQuestionRepository reminderQuestionRepository;
    private final ReminderRepository reminderRepository;
    private final InsightImageRepository insightImageRepository;
    private final HashTagRepository hashTagRepository;
    private final CustomReminderRepository customReminderRepository;
    // S3 클라이언트와 버킷 이름을 주입
    @Autowired
    private S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadImage(Long userId, MultipartFile imageFile) {
        String key = "uploads/" + userId + "/" + imageFile.getOriginalFilename();
        try {
            // S3에 파일 업로드
            PutObjectResponse response = s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize())
            );

            // 업로드된 이미지의 URL 반환
            URL imageUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key));
            return imageUrl.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }


    public List<InsightShareResponseDto> accessSharedFolder( String token) {
        // 토큰 해독
        String decryptedString = AESUtil.decrypt(token);
        String[] parts = decryptedString.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Decrypted data format is incorrect.");
        }

        Long folderId = Long.parseLong(parts[0]);
        boolean isCopyable = Boolean.parseBoolean(parts[1]);
        log.info("toekn = {}, folderId = {}, isCopyable = {}", token, folderId, isCopyable);

        List<Insight> insights = insightRepository.findByFolder_FolderId(folderId);

        List<InsightShareResponseDto> insightShareResponseList = insights.stream().map(insight -> {
            List<String> hashTags = insight.getHashTagList().stream()
                    .map(HashTag::getHashTagName)
                    .collect(Collectors.toList());

            List<String> insightImages = insight.getInsightImageList().stream()
                    .map(InsightImage::getInsightImageUrl)
                    .collect(Collectors.toList());

            Folder folder = folderRepository.findByFolderId(insight.getFolder().getFolderId()).orElseThrow(() -> new IllegalArgumentException("Folder not exists"));

            return InsightShareResponseDto.builder()
                    .insightId(insight.getInsightId())
                    .insightTitle(insight.getInsightTitle())
                    .insightUrl(insight.getInsightUrl())
                    .insightSummary(insight.getInsightSummary())
                    .insightMainImage(insight.getInsightMainImage())
                    .insightMemo(insight.getInsightMemo())
                    .insightSource(insight.getInsightSource())
                    .viewCount(insight.getViewCount())
                    .folderName(insight.getFolder().getFolderName())
                    .folderId(folderId)
                    .isCopyable(isCopyable)
                    .insightTagList(hashTags)
                    .insightImageList(insightImages)
                    .folderColor(folder.getFolderColor())
                    .build();
        }).collect(Collectors.toList());

        return insightShareResponseList;
    }

    @Transactional
    public String getMainImage(Long userId, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements metaOgImage = doc.select("meta[property=og:image]");
            if (!metaOgImage.isEmpty()) {
                return metaOgImage.first().attr("content");
            }
        } catch (Exception e) {
            log.error("Error while fetching main image from URL: {}", url, e);
        }
        return "notExist";
    }

    public List<InsightSimpleResponseDto> getInsightListByFolderAndTag(Long userId, Long folderId, String tag) {
        // 폴더 ID로 Insight 리스트 조회
        List<Insight> insightList = insightRepository.findByInsightFolderId(folderId).orElseGet(Collections::emptyList);

        // Insight 리스트에서 각 Insight의 hashTagList를 확인하여 주어진 태그를 부분적으로 포함하는 Insight만 필터링
        List<InsightSimpleResponseDto> filteredInsightList = insightList.stream()
                .filter(insight -> insight.getHashTagList().stream()
                        .anyMatch(hashTag -> hashTag.getHashTagName().toLowerCase().contains(tag.toLowerCase())))
                .map(insight -> {
                    List<String> hashTagNames = insight.getHashTagList().stream()
                            .map(HashTag::getHashTagName)
                            .collect(Collectors.toList());

                    // FolderColor 가져오기
                    FolderColor folderColor = insight.getFolder().getFolderColor();

                    return InsightSimpleResponseDto.builder()
                            .insightId(insight.getInsightId())
                            .insightMainImage(insight.getInsightMainImage())
                            .insightTitle(insight.getInsightTitle())
                            .insightSummary(insight.getInsightSummary())
                            .insightTagList(hashTagNames)
                            .folderColor(folderColor)
                            .build();
                })
                .collect(Collectors.toList());

        return filteredInsightList;
    }

    public Boolean deleteInsight(Long insightId) {

        Insight insight = insightRepository.findByInsightId(insightId).orElseThrow(() -> new IllegalArgumentException("insight not found"));

        insightRepository.delete(insight);

        return true;
    }

    public void modifyInsight(Long userId, InsightModifyDto dto) {

        // 인사이트 내용 업데이트
        Insight insight = insightRepository.findByInsightId(dto.getInsightId()).orElseThrow(() -> new IllegalArgumentException("Insight not found"));

        insight.setInsightUrl(dto.getInsightUrl());
        insight.setInsightTitle(dto.getInsightTitle());
        insight.setInsightSummary(dto.getInsightSummary());
        insight.setInsightMainImage(dto.getInsightMainImage());
        insight.setInsightMemo(dto.getInsightMemo());
        insight.setInsightSource(dto.getInsightSource());
        insight.setFolder(folderRepository.findByFolderId(dto.getFolderId()).orElseThrow(() -> new IllegalArgumentException("folder not found")));
        insightRepository.save(insight);

        // 기존에 존재하는 해시태그를 모두 삭제 후 재생성
        List<HashTag> hashTagList = hashTagRepository.findByInsight(insight).orElseThrow(() -> new IllegalArgumentException("hashTag not found"));
        for (HashTag tag : hashTagList) {
            hashTagRepository.delete(tag);
        }

        List<String> hashTagNameList = dto.getInsightTagList();
        for (String tag : hashTagNameList) {
            HashTag hashTag = HashTag.builder()
                    .insight(insight)
                    .hashTagName(tag)
                    .build();

            hashTagRepository.save(hashTag);
        }

        // 첨부 이미지 변경
        List<InsightImage> insightImageList = insightImageRepository.findByInsight(insight).orElse(Collections.emptyList());

        for (InsightImage img : insightImageList) {
            insightImageRepository.delete(img);
        }

        List<String> insightImageUrlList = dto.getInsightImageList();
        for (String image : insightImageUrlList) {
            InsightImage insightImage = InsightImage.builder()
                    .insight(insight)
                    .insightImageUrl(image)
                    .build();

            insightImageRepository.save(insightImage);
        }

        // 리마인더 변경
        Reminder reminder = reminderRepository.findByInsight(insight).orElseThrow(() -> new IllegalArgumentException("reminder not found"));
        reminder.setIsEnable(dto.isEnable());
        reminderRepository.save(reminder);

        if (dto.isEnable()) {
            ReminderDate reminderDate = reminderDateRepository.findByReminder(reminder).orElseThrow(() -> new IllegalArgumentException("reminderDate not found"));

            reminderDate.setReminder(reminder);
            reminderDate.setRemindType(dto.getRemindType());
            reminderDate.setRemindDays(dto.getRemindDays());

            reminderDateRepository.save(reminderDate);
        }

        // 질문 답변 변경
        List<Long> questionId = dto.getReminderQuestionId();
        List<String> reminderAnswer = dto.getReminderAnswer();
        int idx = 0;
        for (Long id : questionId) {
            ReminderQuestion reminderQuestion = reminderQuestionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("reminderQuestion not found"));

            reminderQuestion.setReminderAnswer(reminderAnswer.get(idx++));
            reminderQuestionRepository.save(reminderQuestion);
        }
    }

    @Transactional
    public List<InsightSimpleResponseDto> getInsightList(Long userId, Long folderId) {
        List<Insight> insightList = insightRepository.findByInsightFolderId(folderId).orElseGet(Collections::emptyList);

        return insightList.stream().map(insight -> {
            List<HashTag> hashTagList = hashTagRepository.findByInsight(insight).orElseGet(Collections::emptyList);
            List<String> ht = hashTagList.stream()
                    .map(HashTag::getHashTagName)
                    .collect(Collectors.toList());

            // FolderColor 가져오기
            FolderColor folderColor = insight.getFolder().getFolderColor();

            return InsightSimpleResponseDto.builder()
                    .insightId(insight.getInsightId())
                    .insightSummary(insight.getInsightSummary())
                    .insightTitle(insight.getInsightTitle())
                    .insightMainImage(insight.getInsightMainImage())
                    .insightTagList(ht)
                    .folderColor(folderColor)  // 추가된 필드
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public InsightResponseDto getInsightDetail(Long userId, Long insightId) {
        Insight insight = insightRepository.findByInsightId(insightId).orElseThrow(() -> new IllegalArgumentException("Insight not found"));

        Folder folder = folderRepository.findByFolderId(insight.getFolder().getFolderId()).orElseThrow(() -> new IllegalArgumentException("Folder not found"));

        List<HashTag> hashTagList = hashTagRepository.findByInsight(insight).orElseThrow(() -> new IllegalArgumentException("HashTag not found"));

        List<String> hashTagNameList = new ArrayList<>();
        for (HashTag h : hashTagList) {
            hashTagNameList.add(h.getHashTagName());
        }

        // 첨부 이미지는 없을 수도 있음
        List<InsightImage> insightImageList = insightImageRepository.findByInsight(insight).orElse(Collections.emptyList());

        List<String> insightImageNameList = new ArrayList<>();
        for (InsightImage url : insightImageList) {
            insightImageNameList.add(url.getInsightImageUrl());
        }

        Reminder reminder = reminderRepository.findByInsight(insight).orElseThrow(() -> new IllegalArgumentException("Reminder not found"));

        // 조회수 1 증가 및 저장
        incrementViewCount(insight);

        // 리마인더 질문&답변은 없을 수도 있음
        List<ReminderQuestion> reminderQuestionList = reminderQuestionRepository.findByReminder(reminder).orElse(Collections.emptyList());

        // DTO 매핑
        InsightResponseDto.InsightResponseDtoBuilder builder = InsightResponseDto.builder().
                insightId(insight.getInsightId()).
                insightTitle(insight.getInsightTitle()).
                insightUrl(insight.getInsightUrl()).
                insightSummary(insight.getInsightSummary()).
                insightMainImage(insight.getInsightMainImage()).
                insightMemo(insight.getInsightMemo()).
                insightSource(insight.getInsightSource()).
                viewCount(insight.getViewCount()).
                insightTagList(hashTagNameList).
                insightImageList(insightImageNameList).
                isEnable(reminder.getIsEnable()).
                folderName(folder.getFolderName()).folderId(folder.getFolderId()).
                folderColor(folder.getFolderColor()).
                reminderQuestionList(reminderQuestionList);

        if (reminder.getIsEnable()) {
            ReminderDate reminderDate = reminderDateRepository.findByReminder(reminder).orElseThrow(() -> new IllegalArgumentException("ReminderDate not found"));
            builder.remindType(reminderDate.getRemindType()).remindDays(reminderDate.getRemindDays());
            if (customReminderRepository.isInsightRemindersToNotifyV2(userId, insightId)) {
                customReminderRepository.updateLastView(insightId);
            }
        }

        return builder.build();
    }

    private void incrementViewCount(Insight insight) {
        insight.setViewCount(insight.getViewCount() + 1);
        insightRepository.save(insight);
    }

    @Transactional
    public Long saveInsight(Long userId, InsightCreateDto dto) {

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user not found"));

        Optional<Folder> folderOptional = folderRepository.findByFolderNameAndUser(dto.getFolderName(), user);
        if (!folderOptional.isPresent()) {
            folderService.saveFolder(userId, dto.getFolderName(), FolderColor.BLUE);
        }

        Insight insight = Insight.builder().folder(folderRepository.findByFolderNameAndUser(dto.getFolderName(), user).orElseThrow(() -> {
            log.error("[InsightService] folder not found by folderName");
            return new IllegalArgumentException("folder not found");
        })).insightUrl(dto.getInsightUrl()).insightMemo(dto.getInsightMemo()).insightSource(dto.getInsightSource()).insightTitle(dto.getInsightTitle()).insightSummary(dto.getInsightSummary()).insightMainImage(dto.getInsightMainImage()).viewCount(0).build();

        insightRepository.save(insight);


        List<String> insightImageList = dto.getInsightImageList();
        for (String image : insightImageList) {
            InsightImage insightImage = InsightImage.builder().insight(insight).insightImageUrl(image).build();

            insightImageRepository.save(insightImage);

        }

        List<String> hashTagList = dto.getInsightTagList();
        for (String tag : hashTagList) {
            HashTag hashTag = HashTag.builder()
                    .insight(insight)
                    .hashTagName(tag)
                    .build();

            hashTagRepository.save(hashTag);

        }

        Reminder reminder = Reminder.builder().insight(insight).isEnable(dto.isEnable()).lastRemindedAt(LocalDateTime.now()).build();
        log.info("enable = {} ", dto.isEnable());
        reminderRepository.save(reminder);

        ReminderDate reminderDate = ReminderDate.builder()
                .reminder(reminder).
                remindType(dto.getRemindType()).
                remindDays(dto.getRemindDays()).
                build();

        reminderDateRepository.save(reminderDate);

        return insight.getInsightId();
    }

    public List<InsightRecommend> getRecommendInsight(Long userId) {

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("user not found")    );
        Job job = user.getJob();

        return InsightRecommend.getRecommendInsight(job);
    }

    public List<InsightRecommend> getRandRecommendInsight() {
        //todo n+1 문제 및 fetch 최적화
        return insightRepository.randInsight()
                .orElse(Collections.emptyList())
                .stream()
                .map(insight -> InsightRecommend.builder()
                        .insightMainImage(insight.getInsightMainImage())
                        .insightTitle(insight.getInsightTitle())
                        .insightSummary(insight.getInsightSummary())
                        .hashTagList(insight.getHashTagList().stream()
                                .map(HashTag::getHashTagName)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
