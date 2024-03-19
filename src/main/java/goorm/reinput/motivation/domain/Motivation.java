package goorm.reinput.motivation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Motivation {
    @Id
    @GeneratedValue
    private Long motivationId;

    @OneToMany(mappedBy = "motivation")
    private List<Stamp> stamps;
}
