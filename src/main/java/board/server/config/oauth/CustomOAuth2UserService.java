package board.server.config.oauth;

import board.server.app.member.entity.Member;
import board.server.app.member.repository.JdbcTemplateMemberRepository;
import board.server.app.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    // 소셜 로그인 인증 완료
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 인증 후 리소스 서버에서 사용자 정보 로드
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        // registrationId - GOOGLE
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // attributes에 접근하기 위한 키 값
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        CustomOAuth2User oAuth2User = CustomOAuth2UserSocial.of(registrationId, attributes);
        log.info("email: {}, name: {}, role: {}", oAuth2User.getEmail(), oAuth2User.getName(), oAuth2User.getAuthorities());

        return oAuth2User;
    }
}
