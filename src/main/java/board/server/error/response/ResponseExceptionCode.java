package board.server.error.response;

import lombok.*;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;


@Builder
@Getter
public class ResponseExceptionCode{
    private int status;
    private String message;
    private final LocalDateTime date = LocalDateTime.now();

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ValidationError{
        private final String message;
        private final String field;
    }
}
