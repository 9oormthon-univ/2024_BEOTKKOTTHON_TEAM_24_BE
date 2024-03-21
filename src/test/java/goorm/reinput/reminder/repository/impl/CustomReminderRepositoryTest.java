package goorm.reinput.reminder.repository.impl;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightImage;
import goorm.reinput.reminder.domain.Reminder;
import goorm.reinput.reminder.domain.ReminderQuestion;
import goorm.reinput.reminder.domain.dto.ReminderQuestionQueryDto;
import goorm.reinput.user.domain.User;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@ActiveProfiles("dev")
class CustomReminderRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private CustomReminderRepository customReminderRepository;

    private static final LocalDateTime now = LocalDateTime.now();



    @BeforeEach
    public void testEntity(){
        User user1 = User.builder()
                .userEmail("user1")
                .userPassword("password1")
                .userName("name1")
                .build();

        Folder folder1 = Folder.builder()
                .folderName("folder1")
                .user(user1)
                .build();
        Insight insight = Insight.builder()
                .insightTitle("title")
                .insightUrl("url")
                .insightSummary("summary")
                .insightMainImage("mainImage")
                .insightMemo("memo")
                .insightSource("source")
                .folder(folder1)
                .build();
        HashTag hashTag = HashTag.builder()
                .hashTagName("hashTag")
                .insight(insight)
                .build();
        HashTag hashTag2 = HashTag.builder()
                .hashTagName("hashTag2")
                .insight(insight)
                .build();
        InsightImage insightImage = InsightImage.builder()
                .insightImageUrl("image")
                .insight(insight)
                .build();

        em.persist(user1);
        em.persist(folder1);
        em.persist(insight);
        em.persist(hashTag);
        em.persist(hashTag2);
        em.persist(insightImage);
        em.flush();
        em.clear();
        Reminder reminder1 = Reminder.builder()
                .isEnable(true)
                .lastRemindedAt(now)
                .insight(insight)
                .build();

        em.persist(reminder1);

        ReminderQuestion reminderQuestion = ReminderQuestion.builder()
                .reminderQuestion("question")
                .reminder(reminder1)
                .build();

        em.persist(reminderQuestion);

    }

    @Test
    public void findOldestReminderDto() {
        List<ReminderQuestionQueryDto> reminderQuestionQueryDtos = customReminderRepository.findOldestReminderDto();

        assertThat(reminderQuestionQueryDtos.size()).isEqualTo(1);
        assertThat(reminderQuestionQueryDtos.get(0).getInsightTitle()).isEqualTo("title");
        assertThat(reminderQuestionQueryDtos.get(0).getInsightMainImage()).isEqualTo("mainImage");
        assertThat(reminderQuestionQueryDtos.get(0).getInsightTagList().size()).isEqualTo(2);
        assertThat(reminderQuestionQueryDtos.get(0).getReminderQuestion()).isEqualTo("question");
        assertThat(reminderQuestionQueryDtos.get(0).getReminderId()).isNotNull();
        assertThat(reminderQuestionQueryDtos.get(0).getInsightId()).isNotNull();
        assertThat(reminderQuestionQueryDtos.get(0).getInsightTagList().get(0)).isEqualTo("hashTag");
        assertThat(reminderQuestionQueryDtos.get(0).getLastRemindedAt()).isEqualTo(now);
    }




}