package goorm.reinput.insight.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 인사이트 상세보기 dto
@Getter
@NoArgsConstructor
public class InsightResponseDto {

    private Long insightId;
    private Long folderId;
    private InsightCreateDto insight;
}
