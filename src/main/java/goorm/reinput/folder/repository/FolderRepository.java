package goorm.reinput.folder.repository;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.repository.support.FolderRepositoryCustom;
import goorm.reinput.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long>, FolderRepositoryCustom {
    Optional<Folder> findByFolderIdAndUser(Long folderId, User user);
    Optional<Folder> findByFolderNameAndUser(String folderName, User user);
    Optional<Folder> findByFolderId(Long folderId);
}
