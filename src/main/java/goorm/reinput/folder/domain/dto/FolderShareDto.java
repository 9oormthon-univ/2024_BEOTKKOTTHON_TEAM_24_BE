package goorm.reinput.folder.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class FolderShareDto {
    @NotNull(message = "[FolderShareDto] folderId is required")
    private Long folderId;
    @NotNull(message = "[FolderShareDto] copyable is required")
    private boolean copyable;

    public FolderShareDto(Long folderId, boolean copyable) {
        this.folderId = folderId;
        this.copyable = copyable;
    }
}
