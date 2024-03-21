package goorm.reinput.folder.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderShareDto {
    private Long folderId;
    private boolean copyable;

    public FolderShareDto(Long folderId, boolean copyable) {
        this.folderId = folderId;
        this.copyable = copyable;
    }
}
