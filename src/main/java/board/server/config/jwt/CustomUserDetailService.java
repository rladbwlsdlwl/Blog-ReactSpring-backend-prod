package board.server.config.jwt;

import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final JdbcTemplateMemberRepository jdbcTemplateMemberRepository;

    @Autowired
    public CustomUserDetailService(JdbcTemplateMemberRepository jdbcTemplateMemberRepository) {
        this.jdbcTemplateMemberRepository = jdbcTemplateMemberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return jdbcTemplateMemberRepository.findByNameWithRole(username)
                .map(CustomUserDetail:: new)
                .orElseThrow(() -> new BusinessLogicException(CustomExceptionCode.MEMBER_NOT_FOUND));
    }
}
