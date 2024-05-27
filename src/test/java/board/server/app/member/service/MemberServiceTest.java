package board.server.app.member.service;

import board.server.app.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;


    @Test
    public void 중복회원_예외() {
        // GIVEN
        Member member1 = new Member("yujin", "yujin@aaa.aaa", "aaa");
        Member member2 = new Member("yujin1", "yujin@aaa.aaa", "aaa");

        // WHEN
        memberService.join(member1);

        // THEN
        IllegalStateException err = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
    }


}