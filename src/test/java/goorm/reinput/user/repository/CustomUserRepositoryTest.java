package goorm.reinput.user.repository;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.reinput.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class CustomUserRepositoryTest {
    @Autowired
    private EntityManager em;
    private JPAQueryFactory queryFactory;
    @Autowired
    private CustomUserRepository customUserRepository;


    @BeforeEach
    public void testEntity(){
        queryFactory = new JPAQueryFactory(em);
        User user1 = User.builder()
                .userEmail("user1")
                .userPassword("password1")
                .userName("name1")
                .build();
        User user2 = User.builder()
                .userEmail("user2")
                .userPassword("password2")
                .userName("name2")
                .build();
        User user3 = User.builder()
                .userEmail("user3")
                .userPassword("password3")
                .userName("name3")
                .build();
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
    }
    @Test
    public void findByUserEmail() {
        User user = customUserRepository.findByUserEmail("user1").get();
        assertEquals("user1", user.getUserEmail());
    }

    @Test
    public void deactivateUserByUserId() {
        customUserRepository.deactivateUserByUserId(1L);
        User user = customUserRepository.findByUserEmail("user1").get();
        Assertions.assertThat(user.isEnable()).isFalse();
    }


}