package goorm.reinput.insight.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class HashTag extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hashTagId;
    private String hashTagName;

    @ManyToOne(fetch = FetchType.LAZY)
    private Insight insight;

    @Builder
    public HashTag(String hashTagName, Insight insight) {
        this.hashTagName = hashTagName;
        this.insight = insight;
    }

}
