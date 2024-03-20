package goorm.reinput.folder.repository;

import goorm.reinput.folder.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {

}
