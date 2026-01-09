package board.server.config.oauth;

import board.server.app.enums.RoleType;
import board.server.app.member.entity.Member;
import board.server.app.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MemberRepository memberRepository;

    // 소셜 로그인 인증 완료
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 인증 후 리소스 서버에서 사용자 정보 로드
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        // registrationId - GOOGLE, NAVER
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // attributes에 접근하기 위한 키 값
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        CustomOAuth2User oAuth2User = CustomOAuth2UserSocial.of(registrationId, attributes);
        log.info("email: {}, name: {}, role: {}", oAuth2User.getEmail(), oAuth2User.getName(), oAuth2User.getAuthorities());

        return oAuth2User;
    }

    // 비즈니스 로직
    // 소셜 로그인 핸들러 - OAuth2AuthenticationSuccessHandler
    public Member getMember(String email) {
        // 계정 존재여부 확인
        // 존재하지 않는 유저면 강제 회원가입
        return memberRepository.findByEmail(email).orElseGet(() -> createMember(email));
    }

    private Member createMember(String email) {
        // 회원가입 후 토큰 발행
        // 패스워드 NULL

        Member member = Member.builder()
                .email(email)
                .name(getRandomUsername())
                .roleType(RoleType.MEMBER)
                .build();

        memberRepository.save(member);

        return member;
    }


    // 임시 닉네임 - 닉네임 랜덤 생성
    private String getRandomUsername() {
        String[] words = {
                "푸른고양이", "노란강아지", "빨간말", "초록호랑이", "작은돌", "큰사람", "따뜻한별", "차가운개미",
                "밝은토끼", "어두운돼지", "행복한햄스터", "슬픈염소", "빠른조랑말", "느린병아리", "용감한닭", "귀여운거북이"
        };

        Random r = new Random();

        return words[r.nextInt(words.length)] + UUID.randomUUID().toString().substring(0, 5);
    }
}
