package goorm.reinput.user.repository;

import goorm.reinput.user.domain.User;
import goorm.reinput.user.repository.support.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByUserId(Long userId);

}
