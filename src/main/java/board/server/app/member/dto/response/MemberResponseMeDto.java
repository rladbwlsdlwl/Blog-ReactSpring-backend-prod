package board.server.app.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MemberResponseMeDto {
    private Long id;
    private String username;
    private String email;
}
