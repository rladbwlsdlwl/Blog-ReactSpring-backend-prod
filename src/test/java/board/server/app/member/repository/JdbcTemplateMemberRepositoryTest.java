package board.server.app.member.repository;

import board.server.app.member.entity.Member;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class JdbcTemplateMemberRepositoryTest {
    @Autowired
    JdbcTemplateMemberRepository jdbcTemplateUserRepository;


    @Test
    public void 중복이메일검거() throws Exception{
        // GIVEN
        Member member1 = new Member("yujin", "yujin@aaa.aaa", "aaa");
        Member member2 = new Member("yujin", "yujin@aaa.aaa", "aaa");

        // WHEN
        jdbcTemplateUserRepository.save(member1);

        // THEN
        jdbcTemplateUserRepository.findByEmail(member2.getEmail()).ifPresent(err -> {
            Assertions.assertEquals(member1.getEmail(), member2.getEmail());
        });

    }

}