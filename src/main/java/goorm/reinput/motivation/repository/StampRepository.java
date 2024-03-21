package goorm.reinput.motivation.repository;

import goorm.reinput.motivation.domain.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StampRepository extends JpaRepository<Stamp, Long>{
}
