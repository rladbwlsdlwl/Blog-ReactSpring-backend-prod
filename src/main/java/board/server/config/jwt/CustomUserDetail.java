package board.server.config.jwt;

import board.server.app.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetail implements UserDetails {
    private final String name;
    private final String password;
    private final String role;

    public CustomUserDetail(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public CustomUserDetail(Member member) {
        this.name = member.getName();
        this.password = member.getPassword();
        this.role = member.getRoleType().getName(); // role = MEMBER
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role type = "ROLE_USER"
        Collection<? extends GrantedAuthority> auth = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return auth; // ROLE_MEMBER
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
