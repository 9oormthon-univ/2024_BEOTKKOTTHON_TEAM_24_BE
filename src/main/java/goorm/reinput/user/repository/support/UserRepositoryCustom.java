package goorm.reinput.user.repository.support;

import goorm.reinput.user.domain.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByUserEmail(String userEmail);
    void deactivateUserByUserId(Long userId);
    void deleteByUserId(Long userId);
}
