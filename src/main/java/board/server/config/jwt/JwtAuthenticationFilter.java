package board.server.config.jwt;

import board.server.app.member.dto.request.MemberRequestSigninDto;
import board.server.app.member.dto.response.CustomMemberResponseDto;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.errorcode.ExceptionCode;
import board.server.error.response.ResponseExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 인증 시도
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            // Json to Object(class)
            MemberRequestSigninDto memberRequestSigninDto = new ObjectMapper().readValue(request.getInputStream(), MemberRequestSigninDto.class);
            String username = memberRequestSigninDto.getName(), password = memberRequestSigninDto.getPassword();

            // 권한 없는 Authentication 객체 생성
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }catch (IOException | InternalAuthenticationServiceException e){
            log.warn(e.toString());
            failedAuthentication(request, response, e);
        }

        return null;
    }

    // 인증 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String name = authResult.getPrincipal().toString();
        String token = jwtTokenProvider.generateToken(new CustomMemberResponseDto(name));

        log.info("JwtAuthenticationFilter - success to authentication");
        response.addHeader("Authentication", "bearer" + " " + token);
    }

    // 인증 실패 - InternalAuthenticationServiceException의 BusinessLogicException
    // 회원 name Exception - overriding한 loadUserByUsername 별도 처리
    private void failedAuthentication(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.warn("JwtAuthenticationFilter - Failed to Authentication: loadUserByUsername");

        ExceptionCode exceptionCode = CustomExceptionCode.MEMBER_NOT_FOUND;

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(exceptionCode.getStatus());

        ResponseExceptionCode responseExceptionCode = ResponseExceptionCode.builder()
                .status(exceptionCode.getStatus())
                .message(exceptionCode.getMessage())
                .build();

        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseExceptionCode));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // 인증 실패
    // 내부적으로 실행, AuthenticationProvider -> failed to password matching, password = null ....
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn("JwtAuthenticationFilter - Failed to Authentication: not match password, field error...");

        ExceptionCode exceptionCode = CustomExceptionCode.MEMBER_NOT_FOUND;
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(exceptionCode.getStatus());

        ResponseExceptionCode responseExceptionCode = ResponseExceptionCode.builder()
                .status(exceptionCode.getStatus())
                .message(exceptionCode.getMessage())
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(responseExceptionCode));
//        super.unsuccessfulAuthentication(request, response, failed);
    }
}
