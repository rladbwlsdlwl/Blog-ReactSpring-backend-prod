package board.server.app.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
    ROLE_DEFAULT("MEMBER"),
    ROLE_ADMIN("ADMIN");

    private String name;
}
