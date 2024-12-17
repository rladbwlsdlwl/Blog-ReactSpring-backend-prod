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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(CustomOAuth2UserService customOAuth2UserService, JwtTokenProvider jwtTokenProvider) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 처음 가입한 유저는 최소한의 회원 정보로 회원가입 처리
        // 이미 가입한 유저 포함하여 토큰 발급 후 홈화면으로 리다이렉트
        // 소셜로그인으로 회원가입 절차 없이 로그인 가능
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        log.info("oauthAuthenticationSuccessHandler - parsing {}", principal.getEmail());
        
        // 회원 찾기
        Member member = customOAuth2UserService.getMember(principal.getEmail());
        // 토큰 발급
        String token = jwtTokenProvider.generateToken(new CustomMemberResponseDto(member.getName()));
        
        log.info("토큰 발행 성공");
        response.sendRedirect(String.format("https://thisblogproject.vercel.app?token=bearer %s", token));

    }
}
