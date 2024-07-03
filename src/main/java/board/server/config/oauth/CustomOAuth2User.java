package board.server.config.oauth;

import board.server.app.enums.RoleType;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Builder
public class CustomOAuth2User implements OAuth2User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private Map<String, Object> attributes;


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // template -> ROLE_MEMBER
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return authorities;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getEmail(){
        return email;
    }
}
