package board.server.config.jwt;

import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.response.ResponseExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Principal X
        // 토큰 복호화 에러 - RuntimeException
        // 토큰 만료 - ExpiredJwtException
        Exception exception = (Exception) request.getAttribute("exception");


        CommonExceptionCode exceptionCode = CommonExceptionCode.UNAUTHORIZED;

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(exceptionCode.getStatus());

        ResponseExceptionCode responseExceptionCode = ResponseExceptionCode.builder()
                .status(exceptionCode.getStatus())
                .message(exceptionCode.getMessage())
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(responseExceptionCode));

        log.warn("토큰 ERROR!! {}", exception != null ? exception.getMessage(): authException.getMessage());
        authException.getStackTrace();
    }
}
