package board.server.app.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MemberResponseMeDto {
    private Long id;
    private String username;
    private String email;
    private Boolean isNotSettingPassword;
}
