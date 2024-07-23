package board.server.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenBlacklist {
    private Set<String> blacklist = ConcurrentHashMap.newKeySet();

    // 로그아웃 또는 회원정보 수정
    // 기존 토큰을 블랙리스트에 추가
    public void addBlacklist(String token){
        blacklist.add(token);
    }
    public boolean isTokenBlacklisted(String token){
        boolean isBlacklist = blacklist.contains(token);
        if(isBlacklist){
            throw new AccessDeniedException("JwtTokenBlacklist - 접근 거절된 토큰입니다");
        }

        return false;
    }

}
