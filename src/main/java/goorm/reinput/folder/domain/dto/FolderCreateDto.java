package goorm.reinput.folder.domain.dto;

import goorm.reinput.folder.domain.FolderColor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderCreateDto {
    private String folderName;
    private FolderColor folderColor;

    @Builder
    public FolderCreateDto(String folderName, FolderColor folderColor) {
        this.folderName = folderName;
        this.folderColor = folderColor;
    }
}
