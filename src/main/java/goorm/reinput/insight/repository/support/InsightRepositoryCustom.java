package goorm.reinput.insight.repository.support;

import goorm.reinput.insight.domain.Insight;
import goorm.reinput.insight.domain.InsightRecommend;

import java.util.List;
import java.util.Optional;

public interface InsightRepositoryCustom {
    Optional<List<Insight>> randInsight();
    Optional<List<Insight>> findByInsightFolderId(Long folderId);
}
