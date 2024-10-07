package board.server.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

@Slf4j
public class JwtVerificationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenBlacklist jwtTokenBlacklist;


    public JwtVerificationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailService customUserDetailService, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtTokenBlacklist jwtTokenBlacklist) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailService = customUserDetailService;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtTokenBlacklist = jwtTokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader("Authentication");
            token = token.split(" ")[1];

            // check isBlacklist Token (token 무효화)
            isBlacklistToken(token);

            // check access token
            String username = isValidateToken(token);

            // check available path
            isValidatePath(request, username);

        } catch (AccessDeniedException e) {
            log.warn("JwtVerificationFilter - {}", e.getMessage());
            jwtAccessDeniedHandler.handle(request, response, new AccessDeniedException(e.getMessage()));
            return;
        } catch (ExpiredJwtException e) {
            log.warn("JwtVerificationFilter - token 만료!!");
            request.setAttribute("exception", e);
        } catch (RuntimeException e) {
            log.warn("JwtVerificationFilter - 런타임 에러!! 올바르지 않은 토큰");
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    private void isBlacklistToken(String token) {
        // 토큰 무효화 상태 확인
        jwtTokenBlacklist.isTokenBlacklisted(token);
    }

    private String isValidateToken(String token) {
        String username = jwtTokenProvider.getTokenName(token);

        // 토큰 일치여부
        jwtTokenProvider.validateToken(token);

        // 회원의 권한정보를 가져옴
        UserDetails principal = customUserDetailService.loadUserByUsername(username);
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        // 회원 등급에 따른 권한 부여
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, "", authorities);

        // 회원 정보 저장
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        return username;
    }

    private void isValidatePath(HttpServletRequest request, String username) throws UnsupportedEncodingException {
        // 게시판 작성 수정 삭제
        // 좋아요 path는 검사 pass
        // uri의 회원과 토큰의 회원이 일치하는지 확인
        String method = request.getMethod();
        String path = request.getRequestURI().split("/")[2];

        path = URLDecoder.decode(path, "UTF-8");
        log.info("path: {}, method: {}, username: {}", path, method, username);
        if (!username.equals("admin") && !path.equals("likes") && !path.equals("comments") && !path.equals("logout") && !path.equals("auth") && !path.equals(username)) {
            throw new AccessDeniedException("uri와 회원 토큰정보가 일치하지 않습니다");
        }
    }
}
