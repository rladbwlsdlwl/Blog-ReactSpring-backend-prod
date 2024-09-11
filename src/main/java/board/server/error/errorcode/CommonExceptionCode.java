package board.server.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum CommonExceptionCode implements ExceptionCode{
    INVALID_PARAMETER(400, "invalid parameter"),
    FORBIDDEN(403, "Access Denied: You do not have permission to access this resource"),
    UNAUTHORIZED(401, "Access Denied: You should have authentication"),
    FILE_NOT_VALID(500, "파일 읽기 실패! - IOExeption!!"),
    FILE_SIZE_EXCEED(500, "파일 용량 초과! - 용량 제한을 넘었습니다"),
    FILE_TYPE_NOT_VALID(500, "파일 타입 에러! - 올바른 파일 양식을 입력하세요"),
    JSON_PARSING_ERROR(500, "JSON encoding / decoding error!!");


    @Getter
    private final int status;
    @Getter
    private final String message;
}
