package goorm.reinput.user.repository.support;

import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static goorm.reinput.user.domain.QUser.user;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByUserEmail(String userEmail) {
        return Optional.ofNullable(queryFactory.selectFrom(user)
                .where(user.userEmail.eq(userEmail))
                .fetchOne());
    }

    @Override
    public void deactivateUserByUserId(Long userId) {
        queryFactory.update(user)
                .where(user.userId.eq(userId))
                .set(user.isEnable, false)
                .execute();
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory.delete(user)
                .where(user.userId.eq(userId))
                .execute();
    }
}
