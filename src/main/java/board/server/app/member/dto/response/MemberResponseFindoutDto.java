package board.server.app.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberResponseFindoutDto {
    private String username;
    private String password;
}
