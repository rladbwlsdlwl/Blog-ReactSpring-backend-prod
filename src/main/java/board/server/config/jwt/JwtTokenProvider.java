package board.server.config.jwt;

import board.server.app.member.dto.response.CustomMemberResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Getter
@Component
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration_time}")
    private Long expiredTime;

    private final String TOKEN_NAME = "AUTHORIZATION";
    private final String TOKEN_TYPE = "bearer";


    @PostConstruct
    public void init(){
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // create new token
    public String generateToken(CustomMemberResponseDto customMemberResponseDto){
        Claims claims = Jwts.claims();
        claims.put("name", customMemberResponseDto.getName());

        Date date = new Date();
        String jwt = Jwts.builder().setClaims(claims)
                .setSubject(TOKEN_NAME)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + expiredTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return jwt;
    }

    public boolean validateToken(String token){
        try{
            parseClaim(token);
            return true;
        }catch(ExpiredJwtException e) {
            log.info(e.toString());
        }catch(Exception e){
            log.info(e.toString());
        }

        return false;
    }

    public String getTokenName(String token){
        return parseClaim(token).get("name", String.class);
    }

    private Claims parseClaim(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
