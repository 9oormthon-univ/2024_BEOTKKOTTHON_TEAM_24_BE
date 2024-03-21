package goorm.reinput.motivation.repository;

import goorm.reinput.motivation.domain.Motivation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotivationRepository extends JpaRepository<Motivation, Long> {
}
