package goorm.reinput.insight.repository.support;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightRecommend;
import goorm.reinput.insight.repository.CustomInsightRepository;
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
public class InsightRepositoryCustomImpl implements InsightRepositoryCustom{
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    @Autowired
    public InsightRepositoryCustomImpl(EntityManager em){
        this.em = em;
        this.jpaQueryFactory = new JPAQueryFactory(this.em);
    }
    /*
    * 5개의 랜덤 인사이트 반환
     */
    @Override
    public Optional<List<Insight>> randInsight(){

        List<Insight> insights = jpaQueryFactory.selectFrom(insight)
                .orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
                .limit(5)
                .fetch();

        return Optional.ofNullable(insights);
    }

    @Override
    public Optional<List<Insight>> findByInsightFolderId(Long folderId) {
        log.info("[CustomInsightRepository] findByInsightFolderId {} called", folderId);
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(insight)
                .where(insight.folder.folderId.eq(folderId))
                .fetch());
    }
}
