package goorm.reinput.reminder.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Repository
@Slf4j
public class CustomReminderRepository {
    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public CustomReminderRepository(EntityManager em) {
        this.em = em;
        this.jpaQueryFactory = new JPAQueryFactory(this.em);
    }
    //가장 읽은 오래된 리마인더 5개만 조회
    public Optional<List<ReminderQuetionDto>>
}
