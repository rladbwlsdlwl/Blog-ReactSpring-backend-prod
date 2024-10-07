package board.server.app.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberRequestUpdateDto {
    @Size(min = 3, max = 255)
    private String name;
    @Email
    private String email;
    @Size(min = 3, max = 255)
    private String originalPassword;
    @Size(min = 3, max = 255)
    private String password;
}
