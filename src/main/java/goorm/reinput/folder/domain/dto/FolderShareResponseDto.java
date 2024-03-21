package goorm.reinput.folder.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FolderShareResponseDto {
    private String url;
    @Builder
    public FolderShareResponseDto(String url) {
        this.url = url;
    }
}
