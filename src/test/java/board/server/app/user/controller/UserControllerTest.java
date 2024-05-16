package board.server.app.user.controller;

import board.server.app.user.dto.UserRequestDto;
import board.server.app.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder; // DI

    @Test
    void 비밀번호암호화_테스트() {
        // GIVEN
        String pw = "eeee";
        UserRequestDto userRequestDto1 = new UserRequestDto("hello123", "hello123@2", pw);
        UserRequestDto userRequestDto2 = new UserRequestDto("hello123", "hello123@2", pw);

        //WHEN
        String s1 = passwordEncoder.encode(userRequestDto1.getPassword());
        String s2 = passwordEncoder.encode(userRequestDto2.getPassword());

        //THEN
//        Assertions.assertThat(s1).isEqualTo(s2);
        Assertions.assertThat(passwordEncoder.matches(pw, s1)).isEqualTo(passwordEncoder.matches(pw, s2));
    }
}