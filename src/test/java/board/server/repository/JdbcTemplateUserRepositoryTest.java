package board.server.repository;

import board.server.app.user.entity.User;
import board.server.app.user.repository.JdbcTemplateUserRepository;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class JdbcTemplateUserRepositoryTest {
    @Autowired
    JdbcTemplateUserRepository jdbcTemplateUserRepository;


    @Test
    public void 중복이메일검거() throws Exception{
        // GIVEN
        User user1 = new User("yujin", "yujin@aaa.aaa", "aaa");
        User user2 = new User("yujin", "yujin@aaa.aaa", "aaa");

        // WHEN
        jdbcTemplateUserRepository.save(user1);

        // THEN
        jdbcTemplateUserRepository.findByEmail(user2.getEmail()).ifPresent(err -> {
            Assertions.assertEquals(user1.getEmail(), user2.getEmail());
        });

    }

}