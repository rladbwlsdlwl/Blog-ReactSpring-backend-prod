package board.server.app.member.repository;

import board.server.app.member.entity.Member;
import board.server.config.jwt.CustomUserDetail;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class JdbcTemplateMemberRepositoryTest {
    @Autowired
    JdbcTemplateMemberRepository jdbcTemplateMemberRepository;


    @Test
    public void 중복이메일검거() throws Exception{
        // GIVEN
        Member member1 = new Member("yujin", "yujin@aaa.aaa", "aaa");
        Member member2 = new Member("yujin", "yujin@aaa.aaa", "aaa");

        // WHEN
        jdbcTemplateMemberRepository.save(member1);

        // THEN
        jdbcTemplateMemberRepository.findByEmail(member2.getEmail()).ifPresent(err -> {
            Assertions.assertEquals(member1.getEmail(), member2.getEmail());
        });

    }

    @Test
    public void 멤버권한읽기(){
        // GIEVN
        Member member = Member.builder()
                .name("user")
                .email("user1@aaa.aaaa")
                .password("sss")
                .build();

        Member savedMember = jdbcTemplateMemberRepository.save(member);

        String name = null;
        // WHEN
        CustomUserDetail customUserDetail = jdbcTemplateMemberRepository.findByNameAndRole("user")
                .map(CustomUserDetail:: new)
                .orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
        name = customUserDetail.getUsername();
        
        // THEN
        Assertions.assertEquals(savedMember.getName(), customUserDetail.getUsername(), "not equal username");
        Assertions.assertEquals(savedMember.getName(), name, "not equal username");

    }

}