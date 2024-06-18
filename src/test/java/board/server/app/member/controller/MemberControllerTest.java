package board.server.app.member.controller;

import board.server.app.member.dto.request.MemberRequestRegisterDto;
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
        MemberRequestRegisterDto memberRequestRegisterDto1 = new MemberRequestRegisterDto("hello123", "hello123@2", pw);
        MemberRequestRegisterDto memberRequestRegisterDto2 = new MemberRequestRegisterDto("hello123", "hello123@2", pw);

        //WHEN
        String s1 = passwordEncoder.encode(memberRequestRegisterDto1.getPassword());
        String s2 = passwordEncoder.encode(memberRequestRegisterDto2.getPassword());

        //THEN
//        Assertions.assertThat(s1).isEqualTo(s2);
        Assertions.assertThat(passwordEncoder.matches(pw, s1)).isEqualTo(passwordEncoder.matches(pw, s2));
    }
}