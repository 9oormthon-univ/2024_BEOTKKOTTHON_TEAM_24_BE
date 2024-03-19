package goorm.reinput.insight.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class InsightImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insightImageId;
    private String insightImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Insight insight;

    @Builder
    public InsightImage(String insightImageUrl, Insight insight) {
        this.insightImageUrl = insightImageUrl;
        this.insight = insight;
    }

}
