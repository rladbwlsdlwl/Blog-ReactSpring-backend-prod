package board.server.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CustomExceptionCode implements ExceptionCode{
    USER_NOT_FOUND(400, "user not found"),
    USER_DUPLICATE(400, "user is invalidate dulicate");

    @Getter
    private final int status;
    @Getter
    private final String message;

}
