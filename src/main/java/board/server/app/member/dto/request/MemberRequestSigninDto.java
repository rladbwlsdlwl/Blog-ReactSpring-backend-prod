package board.server.app.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestSigninDto {
    @Size(min = 3, max = 255)
    private String name;
    @NotNull
    @Size(min = 3, max = 255)
    private String password;
}
