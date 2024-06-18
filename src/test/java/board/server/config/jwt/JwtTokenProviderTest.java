package board.server.config.jwt;

import board.server.app.member.dto.response.CustomMemberResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class JwtTokenProviderTest {
    @Autowired JwtTokenProvider jwtTokenProvider;

    @Test
    public void 암호화키발급확인(){
        Long time = 3600000L;
        String key = "1"; // 잘못된 키 compare
        Assertions.assertEquals(jwtTokenProvider.getExpiredTime(), time, "load failed: time");
        Assertions.assertNotEquals(jwtTokenProvider.getSecretKey(), key, "load failed: key");
    }

    @Test
    public void 토큰발급(){
        // GIVEN
        CustomMemberResponseDto customMemberResponseDto = new CustomMemberResponseDto("user");


        // WHEN
        String token = jwtTokenProvider.generateToken(customMemberResponseDto);


        // THEN
        Assertions.assertEquals(true, jwtTokenProvider.validateToken(token), "토큰 발급 실패!");
    }

    @Test
    public void 토큰파싱하여회원이름확인(){
        // GIVEN
        CustomMemberResponseDto customMemberResponseDto = new CustomMemberResponseDto("user");
        String token = jwtTokenProvider.generateToken(customMemberResponseDto);


        // WHEN, THEN
        Assertions.assertEquals("user", jwtTokenProvider.getTokenName(token), "파싱 실패 - 이름이 동일하지 않습니다");
    }
}