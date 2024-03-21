package goorm.reinput.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static goorm.reinput.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class CustomUserRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

    public Optional<User> findByUserEmail(String userEmail) {
        return Optional.ofNullable(queryFactory.selectFrom(user)
                .where(user.userEmail.eq(userEmail))
                .fetchOne());
    }
    @Modifying(clearAutomatically = true)
    public void deactivateUserByUserId(Long userId) {
        queryFactory.update(user)
                .where(user.userId.eq(userId))
                .set(user.isEnable, false)
                .execute();
    }

    @Modifying(clearAutomatically = true)
    public void deleteByUserId(Long userId) {
        queryFactory.delete(user)
                .where(user.userId.eq(userId))
                .execute();
    }
}
