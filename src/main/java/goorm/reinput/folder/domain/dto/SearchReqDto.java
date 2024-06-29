package goorm.reinput.folder.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchReqDto {
    @NotBlank(message = "[SearchReqDto] search is required")
    private String search;

    @Builder
    public SearchReqDto(String search) {
        this.search = search;
    }
}
