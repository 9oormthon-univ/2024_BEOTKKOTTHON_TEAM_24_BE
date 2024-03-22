package goorm.reinput.insight.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.repository.FolderRepository;
import goorm.reinput.folder.service.FolderService;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightImage;
import goorm.reinput.insight.domain.dto.InsightCreateDto;
import goorm.reinput.insight.domain.dto.InsightModifyDto;
import goorm.reinput.insight.domain.dto.InsightResponseDto;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
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

    public List<InsightSimpleResponseDto> getInsightListByFolderAndTag(Long userId, Long folderId, String tag) {
        // 폴더 ID로 Insight 리스트 조회
        List<Insight> insightList = customInsightRepository.findByInsightFolderId(folderId).orElseGet(Collections::emptyList);

        // Insight 리스트에서 각 Insight의 hashTagList를 확인하여 주어진 태그를 부분적으로 포함하는 Insight만 필터링
        List<InsightSimpleResponseDto> filteredInsightList = insightList.stream()
                .filter(insight -> insight.getHashTagList().stream()
                        .anyMatch(hashTag -> hashTag.getHashTagName().toLowerCase().contains(tag.toLowerCase())))
                .map(insight -> new InsightSimpleResponseDto(
                        insight.getInsightId(),
                        insight.getInsightMainImage(),
                        insight.getInsightTitle(),
                        insight.getInsightSummary(),
                        insight.getHashTagList().stream().map(HashTag::getHashTagName).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return filteredInsightList;
    }
    public Boolean deleteInsight(Long insightId){

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

        List<String> hashTagNameList = dto.getHashTagList();
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
        for(Long id : questionId){
            ReminderQuestion reminderQuestion = reminderQuestionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("reminderQuestion not found")    );

            reminderQuestion.setReminderAnswer(reminderAnswer.get(idx++));
            reminderQuestionRepository.save(reminderQuestion);
        }
    }

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
                hashTagList(hashTagNameList).
                insightImageList(insightImageNameList).
                isEnable(reminder.getIsEnable()).
                folderName(folder.getFolderName()).folderId(folder.getFolderId()).
                reminderQuestionList(reminderQuestionList);

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

        ReminderDate reminderDate = ReminderDate.builder()
                .reminder(reminder).
                remindType(dto.getRemindType()).
                remindDays(dto.getRemindDays()).
                build();

        reminderDateRepository.save(reminderDate);
    }

}
