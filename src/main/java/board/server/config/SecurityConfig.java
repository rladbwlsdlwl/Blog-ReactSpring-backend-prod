package board.server.config;

import board.server.config.jwt.*;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(PasswordEncoder passwordEncoder, CustomUserDetailService customUserDetailService, JwtTokenProvider jwtTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailService = customUserDetailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception{
        JwtAccessDeniedHandler jwtAccessDeniedHandler = new JwtAccessDeniedHandler();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenProvider);
        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenProvider, customUserDetailService, jwtAccessDeniedHandler);
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

        // 글 작성, 수정, 삭제는 회원만 가능
        // 회원 수정, 탈퇴는 회원만 가능
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/signup").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/{user}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/{user}/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "api/{user}/{boardId}").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/{user}/setting").hasAnyRole("MEMBER")
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
