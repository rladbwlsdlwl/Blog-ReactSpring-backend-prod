package board.server.app.member.dto.response;

import board.server.app.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomMemberResponseDto {
    private String name;
    private final RoleType roleType = RoleType.MEMBER;
}
