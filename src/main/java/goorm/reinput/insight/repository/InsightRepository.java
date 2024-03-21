package goorm.reinput.insight.repository;

import goorm.reinput.insight.domain.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InsightRepository extends JpaRepository<Insight, Long> {
    Optional<Insight> findByInsightId(Long insightId);
}
