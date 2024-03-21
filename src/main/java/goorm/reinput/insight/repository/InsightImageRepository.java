package goorm.reinput.insight.repository;

import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsightImageRepository extends JpaRepository<InsightImage, Long> {
    Optional<List<InsightImage>> findByInsight(Insight insight);
}
