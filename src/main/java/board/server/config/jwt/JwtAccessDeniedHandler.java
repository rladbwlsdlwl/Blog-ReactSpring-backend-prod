package board.server.config.jwt;

import board.server.error.errorcode.CommonExceptionCode;
import board.server.error.errorcode.ExceptionCode;
import board.server.error.response.ResponseExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Principal O
        // UserAuthority - 접근권한이 없음
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ExceptionCode code = CommonExceptionCode.FORBIDDEN;
        ResponseExceptionCode responseExceptionCode = ResponseExceptionCode.builder()
                .status(code.getStatus())
                .message(code.getMessage())
                .build();

        response.getWriter().write(new ObjectMapper().writeValueAsString(responseExceptionCode));
        log.warn(accessDeniedException.getMessage());
    }
}
