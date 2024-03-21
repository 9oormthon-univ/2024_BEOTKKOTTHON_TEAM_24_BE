package goorm.reinput.folder.repository;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Optional<Folder> findByFolderIdAndUser(Long folderId, User user);
    Optional<Folder> findByFolderName(String folderName);
    Optional<Folder> findByFolder(Folder folder);

}
