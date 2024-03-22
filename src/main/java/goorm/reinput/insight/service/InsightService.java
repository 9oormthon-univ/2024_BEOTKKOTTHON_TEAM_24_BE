package goorm.reinput.insight.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.folder.service.FolderService;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightImage;
import goorm.reinput.insight.domain.dto.InsightCreateDto;
import goorm.reinput.insight.domain.dto.InsightResponseDto;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
import goorm.reinput.insight.repository.CustomInsightRepository;
import goorm.reinput.insight.repository.HashTagRepository;
import goorm.reinput.insight.repository.InsightImageRepository;
import goorm.reinput.insight.repository.InsightRepository;
import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderDate;
import goorm.reinput.reminder.repository.ReminderDateRepository;
import goorm.reinput.reminder.repository.ReminderQuestionRepository;
import goorm.reinput.reminder.repository.ReminderRepository;
import goorm.reinput.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final CustomInsightRepository customInsightRepository;

    @Transactional
    public List<InsightSimpleResponseDto> getInsightList(Long userId, Long folderId) {
        List<Insight> insightList = customInsightRepository.findByInsightFolderId(folderId).orElseGet(Collections::emptyList);

        return insightList.stream().map(insight -> {

            List<HashTag> hashTagList = hashTagRepository.findByInsight(insight).orElseGet(Collections::emptyList);
            List<String> ht = new ArrayList<>();
            for (HashTag h : hashTagList) {
                ht.add(h.getHashTagName());
            }

            return InsightSimpleResponseDto.builder().insightId(insight.getInsightId()).insightSummary(insight.getInsightSummary()).insightTitle(insight.getInsightTitle()).insightMainImage(insight.getInsightMainImage()).hashTagList(ht).build();
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

        List<InsightImage> insightImageList = insightImageRepository.findByInsight(insight).orElseThrow(() -> new IllegalArgumentException("Insight images not found"));

        List<String> insightImageNameList = new ArrayList<>();
        for (InsightImage url : insightImageList) {
            insightImageNameList.add(url.getInsightImageUrl());
        }

        Reminder reminder = reminderRepository.findByInsight(insight).orElseThrow(() -> new IllegalArgumentException("Reminder not found"));

        // 조회수 1 증가 및 저장
        incrementViewCount(insight);

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
                hashTagList(hashTagNameList).
                insightImageList(insightImageNameList).
                isEnable(reminder.getIsEnable()).
                folderName(folder.getFolderName()).folderId(folder.getFolderId());

        if (reminder.getIsEnable()) {
            ReminderDate reminderDate = reminderDateRepository.findByReminder(reminder).orElseThrow(() -> new IllegalArgumentException("ReminderDate not found"));
            builder.remindType(reminderDate.getRemindType()).remindDays(reminderDate.getRemindDays());
        }

        return builder.build();
    }

    private void incrementViewCount(Insight insight) {
        insight.setViewCount(insight.getViewCount() + 1);
        insightRepository.save(insight);
    }

    @Transactional
    public void saveInsight(Long userId, InsightCreateDto dto) {

        Optional<Folder> folderOptional = folderRepository.findByFolderName(dto.getFolderName());
        if (!folderOptional.isPresent()) {
            folderService.saveFolder(userId, dto.getFolderName(), FolderColor.BLUE);
        }


        Insight insight = Insight.builder().folder(folderRepository.findByFolderName(dto.getFolderName()).orElseThrow(() -> {
            log.error("[InsightService] folder not found by folderName");
            return new IllegalArgumentException("folder not found");
        })).insightUrl(dto.getInsightUrl()).insightMemo(dto.getInsightMemo()).insightSource(dto.getInsightSource()).insightTitle(dto.getInsightTitle()).insightSummary(dto.getInsightSummary()).insightMainImage(dto.getInsightMainImage()).viewCount(0).build();

        insightRepository.save(insight);


        List<String> insightImageList = dto.getInsightImageList();
        for (String image : insightImageList) {
            InsightImage insightImage = InsightImage.builder().insight(insight).insightImageUrl(image).build();

            insightImageRepository.save(insightImage);

        }

        List<String> hashTagList = dto.getHashTagList();
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


        if (dto.isEnable()) {
            ReminderDate reminderDate = ReminderDate.builder()
                            .reminder(reminder).
                    remindType(dto.getRemindType()).
                    remindDays(dto.getRemindDays()).
                    build();

            reminderDateRepository.save(reminderDate);
        }
    }
}
