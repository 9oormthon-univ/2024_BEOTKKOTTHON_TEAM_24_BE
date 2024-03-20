package goorm.reinput.folder.domain.dto;

import goorm.reinput.folder.domain.FolderColor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderResponseDto {
    private Long folderId;
    private String folderName;
    private FolderColor folderColor;
    private Long insightCount;

    @Builder
    public FolderResponseDto(Long folderId, String folderName, FolderColor folderColor, Long insightCount) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.folderColor = folderColor;
        this.insightCount = insightCount;
    }
}
