package board.server.app.member.controller;

import board.server.app.member.dto.MemberRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder; // DI

    @Test
    void 비밀번호암호화_테스트() {
        // GIVEN
        String pw = "eeee";
        MemberRequestDto memberRequestDto1 = new MemberRequestDto("hello123", "hello123@2", pw);
        MemberRequestDto memberRequestDto2 = new MemberRequestDto("hello123", "hello123@2", pw);

        //WHEN
        String s1 = passwordEncoder.encode(memberRequestDto1.getPassword());
        String s2 = passwordEncoder.encode(memberRequestDto2.getPassword());

        //THEN
//        Assertions.assertThat(s1).isEqualTo(s2);
        Assertions.assertThat(passwordEncoder.matches(pw, s1)).isEqualTo(passwordEncoder.matches(pw, s2));
    }
}