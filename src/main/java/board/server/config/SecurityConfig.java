package board.server.config;

import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.repository.MemberRepository;
import board.server.app.role.repository.RoleRepository;
import board.server.config.jwt.*;
import board.server.config.oauth.CustomOAuth2UserService;
import board.server.config.oauth.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenBlacklist jwtTokenBlacklist;

    public SecurityConfig(PasswordEncoder passwordEncoder, CustomUserDetailService customUserDetailService, CustomOAuth2UserService customOAuth2UserService, JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository, RoleRepository roleRepository, JwtTokenBlacklist jwtTokenBlacklist) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailService = customUserDetailService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenBlacklist = jwtTokenBlacklist;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception{
        JwtAccessDeniedHandler jwtAccessDeniedHandler = new JwtAccessDeniedHandler();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider);
        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenProvider, customUserDetailService, jwtAccessDeniedHandler, jwtTokenBlacklist);
        OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler = new OAuth2AuthenticationSuccessHandler(customOAuth2UserService, jwtTokenProvider);

        jwtAuthenticationFilter.setFilterProcessesUrl("/api/signin");

        // 위조 확인
        http.csrf((csrf) -> csrf.disable());
        http.cors(Customizer.withDefaults());

        // session 비활성화
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin((form -> form.disable()));
        http.httpBasic(AbstractHttpConfigurer::disable);

        // 토큰이 없거나 (만료된 경우) 권한이 없는 회원일경우 실행
        http.exceptionHandling(exceptionHandler -> exceptionHandler
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(jwtAccessDeniedHandler)
        );

        http.addFilter(jwtAuthenticationFilter);
        http.addFilterAfter(jwtVerificationFilter, UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(oauth ->
                // OAuth2 로그인 성공 후 처리할 클래스
                oauth.userInfoEndpoint(c -> c.userService(new CustomOAuth2UserService()))
                        // 회원가입 리다이렉트 or 로그인 성공 토큰 발급 처리
                        .successHandler(oAuth2AuthenticationSuccessHandler)
        );

        // 글 작성, 수정, 삭제는 회원만 가능
        // 회원 수정, 탈퇴는 회원만 가능
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/findInfo/id").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/api/findInfo/pw").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/logout").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/auth/me").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/{user}/setting").hasAnyRole("MEMBER")
                .requestMatchers(HttpMethod.DELETE, "/api/{user}/setting").hasAnyRole("MEMBER")
                .requestMatchers(HttpMethod.POST, "/api/{user}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/{user}/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "api/{user}/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/{user}/file/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/{user}/file/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/{user}/file/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/likes/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/likes/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/comments/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "api/comments/{commentsId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/comments/{commentsId}").hasAnyRole("MEMBER", "ADMIN")
                .anyRequest().permitAll()
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        
        authenticationManagerBuilder
                .userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }
}
