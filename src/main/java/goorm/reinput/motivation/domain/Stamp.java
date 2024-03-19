package goorm.reinput.motivation.domain;

import goorm.reinput.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Stamp extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long stampId;
    private Date stampDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Motivation motivation;

    @Builder
    public Stamp(Date stampDate, Motivation motivation) {
        this.stampDate = stampDate;
        this.motivation = motivation;
    }
}
