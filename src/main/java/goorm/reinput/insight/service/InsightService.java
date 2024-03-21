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

import goorm.reinput.insight.repository.HashTagRepository;
import goorm.reinput.insight.repository.InsightImageRepository;
import goorm.reinput.insight.repository.InsightRepository;
import goorm.reinput.reminder.domain.RemindType;
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
import java.util.List;
import java.util.Optional;

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

    public InsightResponseDto getInsightDetail(Long userId, Long insightId) {
        Optional<Insight> insight = insightRepository.findByInsightId(insightId);

        // InsightResponseDto에 값을 매핑하는 작업이 필요함


    }

    @Transactional
    public void saveInsight(Long userId, InsightCreateDto dto) {
        // 1. folderName이 기존애 존재하지 않을 경우, 먼저 폴더를 생성합니다.
        Optional<Folder> folderOptional = folderRepository.findByFolderName(dto.getFolderName());
        if (!folderOptional.isPresent()) {
            folderService.saveFolder(userId, dto.getFolderName(), FolderColor.BLUE);
        }

        // 2. folderName이 존재할 경우, builder()을 사용해 Insight을 만들고 save 합니다.
        Insight insight = Insight.builder()
                .folder(folderRepository.findByFolderName(dto.getFolderName()).orElseThrow(() -> {
                    log.error("[InsightService] folder not found by folderName");
                    return new IllegalArgumentException("folder not found");
                }))
                .insightUrl(dto.getInsightUrl())
                .insightMemo(dto.getInsightMemo())
                .insightSource(dto.getInsightSource())
                .insightTitle(dto.getInsightTitle())
                .insightSummary(dto.getInsightSummary())
                .insightMainImage(dto.getInsightMainImage())
                .viewCount(0)
                .build();

        insightRepository.save(insight);

        // 3. 사진 리스트를 저장합니다.
        List<InsightImage> insightImageList = dto.getInsightImageList();
        for (InsightImage image : insightImageList) {
            InsightImage insightImage = InsightImage.builder()
                    .insightImageUrl(image.getInsightImageUrl())
                    .build();

            insightImageRepository.save(insightImage);
        }

        // 4. 해시태그 리스트를 저장합니다.
        List<HashTag> hashTagList = dto.getHashTagList();
        for (HashTag tag : hashTagList) {
            HashTag hashTag = HashTag.builder()
                    .hashTagName(tag.getHashTagName())
                    .build();

            hashTagRepository.save(hashTag);
        }

        // reminder을 생성합니다.
        Reminder reminder = Reminder.builder()
                .insight(insight)
                .isEnable(dto.isEnable())
                .lastRemindedAt(LocalDateTime.now())
                .build();

        reminderRepository.save(reminder);

        // reminder-date를 생성합니다. isEnable이 false인 경우 생성하지 않습니다.
        if (dto.isEnable()) {
            ReminderDate reminderDate = ReminderDate.builder()
                    .reminder(reminder)
                    .remindType(dto.getRemindType())
                    .remindDays(dto.getRemindDays())
                    .build();

            reminderDateRepository.save(reminderDate);
        }
    }
}
