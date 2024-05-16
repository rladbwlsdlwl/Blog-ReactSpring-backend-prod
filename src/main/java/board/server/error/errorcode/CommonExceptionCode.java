package board.server.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum CommonExceptionCode implements ExceptionCode{
    INVALID_PARAMETER(400, "invalid parameter");

    @Getter
    private final int status;
    @Getter
    private final String message;
}
