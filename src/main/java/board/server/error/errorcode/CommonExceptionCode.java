package board.server.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum CommonExceptionCode implements ExceptionCode{
    INVALID_PARAMETER(400, "invalid parameter"),
    FORBIDDEN(403, "Access Denied: You do not have permission to access this resource"),
    UNAUTHORIZED(401, "Access Denied: You should have authentication"),
    FILE_NOT_VALID(500, "파일 읽기 실패! - IOExeption!!");

    @Getter
    private final int status;
    @Getter
    private final String message;
}
