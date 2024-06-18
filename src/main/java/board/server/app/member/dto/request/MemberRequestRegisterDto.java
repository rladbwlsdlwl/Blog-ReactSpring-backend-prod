package board.server.app.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberRequestRegisterDto {
    @NotNull
    @Size(min = 3, max = 255)
    private String name;
    @Email
    @NotEmpty
    private String email;
    @NotNull
    @Size(min = 3, max = 255)
    private String password;
}
