package goorm.reinput.folder.service;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import goorm.reinput.folder.domain.dto.FolderDto;
import goorm.reinput.folder.domain.dto.FolderResponseDto;
import goorm.reinput.user.domain.User;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        em.persist(folder);
        em.persist(folder2);
        em.flush();
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


}