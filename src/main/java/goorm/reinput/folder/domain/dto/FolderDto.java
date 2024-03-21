package goorm.reinput.folder.domain.dto;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderDto {
    @NotNull
    private Long folderId;
    private String folderName;
    private FolderColor folderColor;

    @Builder
    public FolderDto(Long folderId, String folderName, FolderColor folderColor) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderColor = folderColor;
    }
}
