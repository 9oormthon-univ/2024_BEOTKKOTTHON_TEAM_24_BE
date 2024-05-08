package goorm.reinput.folder.domain.dto;

import goorm.reinput.folder.domain.Folder;
import goorm.reinput.folder.domain.FolderColor;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderDto {
    @NotNull
    private Long folderId;
    @Nullable
    private String folderName;
    @Nullable
    private FolderColor folderColor;

    @Builder
    public FolderDto(Long folderId, @Nullable String folderName, @Nullable FolderColor folderColor) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderColor = folderColor;
    }
}
