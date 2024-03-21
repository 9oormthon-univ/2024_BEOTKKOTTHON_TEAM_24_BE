package goorm.reinput.insight.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.insight.domain.Insight;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static goorm.reinput.insight.domain.QInsight.insight;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomInsightRepository {
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public CustomInsightRepository(EntityManager em) {
        this.em = em;
        this.jpaQueryFactory = new JPAQueryFactory(this.em);
    }

    public Optional<List<Insight>> findByInsightFolderId(Long folderId) {
        log.info("[CustomInsightRepository] findByInsightFolderId {} called", folderId);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(insight)
                .where(insight.folder.folderId.eq(folderId))
                .fetch());
    }

}
