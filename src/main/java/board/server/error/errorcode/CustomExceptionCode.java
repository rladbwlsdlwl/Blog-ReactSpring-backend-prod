package board.server.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CustomExceptionCode implements ExceptionCode{
    USER_NOT_FOUND(400, "user not found"),
    USER_DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다"),
    USER_DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다"),

    BOARD_NOT_FOUND(400, "존재하지 않는 게시판입니다");

    @Getter
    private final int status;
    @Getter
    private final String message;

}
