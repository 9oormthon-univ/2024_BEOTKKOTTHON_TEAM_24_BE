package goorm.reinput.insight.repository;

import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.repository.support.InsightRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsightRepository extends JpaRepository<Insight, Long>, InsightRepositoryCustom {
    Optional<Insight> findByInsightId(Long insightId);
    List<Insight> findByFolder_FolderId(Long folderId);

}
