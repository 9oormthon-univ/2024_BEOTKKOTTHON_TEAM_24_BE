package goorm.reinput.folder.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.dto.InsightSimpleResponseDto;
import goorm.reinput.user.domain.User;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class FolderServiceTest {

    @Autowired
    private FolderService folderService;

    @Autowired
    EntityManager em;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .userEmail("user@mail.com")
                .userName("user")
                .userPassword("password")
                .build();

        em.persist(user);
        em.flush();
        em.clear();

        Folder folder = Folder.builder()
                .user(user)
                .folderName("folder")
                .folderColor(FolderColor.PINK)
                .build();
        Folder folder2 = Folder.builder()
                .user(user)
                .folderName("folder2")
                .folderColor(FolderColor.BLUE)
                .build();
        // Insight 객체 생성 및 저장
        List<Insight> insights = Arrays.asList(
                Insight.builder().folder(folder).insightTitle("test Insight 1").insightUrl("url1").insightMemo("Memo about testing").insightSummary("Summary test 1").viewCount(10).insightMainImage("image1.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Second Insight").insightUrl("url2").insightMemo("Another test memo").insightSummary("Another summary").viewCount(5).insightMainImage("image2.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Third test").insightUrl("url3").insightMemo("Memo for the third test").insightSummary("Test summary 3").viewCount(15).insightMainImage("image3.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Fourth Insight").insightUrl("url4").insightMemo("Test memo four").insightSummary("Fourth summary").viewCount(20).insightMainImage("image4.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Fifth test insight").insightUrl("url5").insightMemo("Fifth test memo").insightSummary("Fifth summary test").viewCount(25).insightMainImage("image5.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Sixth Insight").insightUrl("url6").insightMemo("Sixth memo").insightSummary("Sixth summary").viewCount(30).insightMainImage("image6.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Seventh Insight test").insightUrl("url7").insightMemo("Seventh test memo").insightSummary("Test seventh summary").viewCount(35).insightMainImage("image7.jpg").build(),
                Insight.builder().folder(folder).insightTitle("Eighth test").insightUrl("url8").insightMemo("Eighth memo about test").insightSummary("Eighth test summary").viewCount(40).insightMainImage("image8.jpg").build()
        );

        insights.forEach(em::persist);
        em.persist(folder);
        em.persist(folder2);

        em.flush();
        em.clear();
    }

    @Test
    void getFolderList() {
        // given
        Long userId = 1L;

        // when
        List<FolderResponseDto> folderResponseDtoList = folderService.getFolderList(userId);

        // then
        assertThat(folderResponseDtoList.size()).isEqualTo(2);
        assertThat(folderResponseDtoList.get(0).getFolderName()).isEqualTo("folder");
        assertThat(folderResponseDtoList.get(1).getFolderName()).isEqualTo("folder2");

    }

    @Test
    void saveFolder(){
        // given
        String folderName = "test";
        FolderColor folderColor = FolderColor.PINK;

        // when
        folderService.saveFolder(1L, folderName, folderColor);

        // then
        assertThat(folderService.getFolderList(1L).size()).isEqualTo(3);
        assertThat(folderService.getFolderList(1L).get(2).getFolderName()).isEqualTo(folderName);
    }

    @Test
    void updateFolder(){
        // given
        String folderName = "testupdate";
        FolderColor folderColor = FolderColor.PINK;
        FolderDto folderDto = FolderDto.builder()
                .folderId(2L)
                .folderName(folderName)
                .folderColor(folderColor)
                .build();
        // when
        folderService.updateFolder(1L, folderDto);

        // then
        assertThat(folderService.getFolderList(1L).size()).isEqualTo(2);
        assertThat(folderService.getFolderList(1L).get(1).getFolderName()).isEqualTo(folderName);
    }

    @Test
    void deleteFolder(){
        // given
        Long folderId = 2L;

        // when
        folderService.deleteFolder(1L, folderId);

        // then
        assertThat(folderService.getFolderList(1L).size()).isEqualTo(1);
    }

    @Test
    void searchInsight(){
        // given
        String searchWord = "test";
        // when
        List<InsightSimpleResponseDto> insightSimpleResponseDtoList = folderService.findAllInsights(1L, searchWord);

        // then
        assertThat(insightSimpleResponseDtoList.size()).isEqualTo(6);
        // 우선순위대로 정렬되어 있는지 확인 제목 8점 요약 4점 태그 2점 메모 1점
        assertThat(insightSimpleResponseDtoList.get(0).getInsightTitle()).isEqualTo("test Insight 1");
        assertThat(insightSimpleResponseDtoList.get(1).getInsightTitle()).isEqualTo("Third test");
        assertThat(insightSimpleResponseDtoList.get(2).getInsightTitle()).isEqualTo("Fifth test insight");
        assertThat(insightSimpleResponseDtoList.get(3).getInsightTitle()).isEqualTo("Seventh Insight test");
        assertThat(insightSimpleResponseDtoList.get(4).getInsightTitle()).isEqualTo("Eighth test");
        assertThat(insightSimpleResponseDtoList.get(5).getInsightTitle()).isEqualTo("Second Insight");


    }


}