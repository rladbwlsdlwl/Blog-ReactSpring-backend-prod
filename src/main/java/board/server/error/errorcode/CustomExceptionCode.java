package board.server.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CustomExceptionCode implements ExceptionCode{
    MEMBER_NOT_FOUND(400, "유저를 찾을 수 없습니다"),
    MEMBER_DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다"),
    MEMBER_DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다"),
    MEMBER_NO_PERMISSION(400, "올바른 접근이 아닙니다"),
    MEMBER_AUTH_NICKNAME(400, "5자 이상의 닉네임을 입력하세요"),
    MEMBER_AUTH_PASSWORD(400, "5자 이상의 패스워드를 입력하세요"),
    BOARD_NOT_FOUND(400, "존재하지 않는 게시글입니다");

    @Getter
    private final int status;
    @Getter
    private final String message;

}
