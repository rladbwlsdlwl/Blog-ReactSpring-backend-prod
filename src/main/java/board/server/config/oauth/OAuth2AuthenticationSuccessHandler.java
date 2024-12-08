package board.server.config.oauth;

import board.server.app.enums.RoleType;
import board.server.app.member.dto.response.CustomMemberResponseDto;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.entity.Role;
import board.server.app.role.repository.RoleRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(MemberRepository memberRepository, RoleRepository roleRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
//    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 처음 가입한 유저는 최소한의 회원 정보로 회원가입 처리
        // 이미 가입한 유저 포함하여 토큰 발급 후 홈화면으로 리다이렉트
        // 소셜로그인으로 회원가입 절차 없이 로그인 가능
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        log.info("oauthAuthenticationSuccessHandler - parsing {}", principal.getEmail());

        String email = principal.getEmail();
        String name = getRandomUsername();
        final String[] token = {""};

        memberRepository.findByEmail(email).ifPresentOrElse(member -> {
            // 토큰 발행
            token[0] = jwtTokenProvider.generateToken(new CustomMemberResponseDto(member.getName()));
        }, () -> {
            // 회원가입 후 토큰 발행
            // 패스워드 NULL
            Member member = Member.builder()
                    .email(email)
                    .name(name)
                    .build();
            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.MEMBER)
                    .build();

            memberRepository.save(member);
            roleRepository.save(role);

            token[0] = jwtTokenProvider.generateToken(new CustomMemberResponseDto(member.getName()));
        });


        log.info("토큰 발행 성공");
        response.sendRedirect(String.format("https://thisblogproject.vercel.app?token=bearer %s", token[0]));

    }

    private String getRandomUsername() {
        String[] words = {
                "푸른고양이", "노란강아지", "빨간말", "초록호랑이", "작은돌", "큰사람", "따뜻한별", "차가운개미",
                "밝은토끼", "어두운돼지", "행복한햄스터", "슬픈염소", "빠른조랑말", "느린병아리", "용감한닭", "귀여운거북이"
        };

        Random r = new Random();

        return words[r.nextInt(words.length)] + UUID.randomUUID().toString().substring(0, 5);
    }
}
