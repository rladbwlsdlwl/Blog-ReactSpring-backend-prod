package board.server.config.oauth;

import board.server.app.member.dto.response.CustomMemberResponseDto;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.config.jwt.JwtTokenProvider;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JdbcTemplateMemberRepository jdbcTemplateMemberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(JdbcTemplateMemberRepository jdbcTemplateMemberRepository, JwtTokenProvider jwtTokenProvider) {
        this.jdbcTemplateMemberRepository = jdbcTemplateMemberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 이미 가입한 유저(토큰 발급 후 홈화면으로 리다이렉트) || 처음 가입하는 유저(회원가입 페이지로 리다이렉트)
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        log.info("oauthAuthenticationSuccessHandler - parsing {}", principal.getEmail());

        String email = principal.getEmail();
        String name = principal.getName();
        final String[] token = {""};

        jdbcTemplateMemberRepository.findByEmail(email).ifPresent((member) -> {
            token[0] = jwtTokenProvider.generateToken(new CustomMemberResponseDto(member.getName()));
        });


        if(!token[0].isEmpty()){
            log.info("토큰 발행 성공");
            response.sendRedirect(String.format("http://localhost:3000?token=bearer %s", token[0]));
        }else{
            log.info("회원가입페이지로 리다이렉트");
            response.sendRedirect(String.format("http://localhost:3000/signup?email=%s", email));
        }
    }
}
