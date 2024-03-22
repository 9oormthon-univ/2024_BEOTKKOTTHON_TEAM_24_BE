package goorm.reinput.folder.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchReqDto {
    private String search;

    @Builder
    public SearchReqDto(String search) {
        this.search = search;
    }
}
