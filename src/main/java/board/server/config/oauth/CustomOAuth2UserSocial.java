package board.server.config.oauth;

import board.server.error.errorcode.CustomExceptionCode;
import board.server.error.exception.BusinessLogicException;

import java.util.Map;

public class CustomOAuth2UserSocial {
    private Map<String, Object> attributes;
    private String registrationId;
    private String usernameAttributeName;

    public static CustomOAuth2User of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            default -> throw new BusinessLogicException(CustomExceptionCode.MEMBER_NO_PERMISSION);
        };
    }

    private static CustomOAuth2User ofGoogle(Map<String, Object> attributes) {
        return CustomOAuth2User.builder()
                .email((String) attributes.get("email"))
                .name(String.valueOf(attributes.get("name")))
                .attributes(attributes)
                .build();
    }


}
