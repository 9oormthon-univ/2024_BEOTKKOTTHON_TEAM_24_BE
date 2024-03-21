package goorm.reinput.insight.repository;

import goorm.reinput.insight.domain.HashTag;
import goorm.reinput.insight.domain.Insight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HashTagRepository extends JpaRepository<HashTag, Long>{
    Optional<List<HashTag>> findByInsight(Insight insight);
}
