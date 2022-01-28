package team.project.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import java.util.Date;

@Service
@PropertySource("classpath:jwt.properties")
public class JwtToken {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private int tokenValidTime;

    public String createToken(Long memberId){
        Date now = new Date();
        return Jwts.builder()
                .claim("loginMember", memberId) // body
                .setIssuedAt(now) // 생성시간
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간
                .signWith(SignatureAlgorithm.HS256, secret) // 알고리즘 종류
                .compact(); // 발급
    }

    public Long getMemberId(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token).getBody().get("loginMember",Long.class);
    }

    public String extractToken(String authorizationToken){
        tokenEmpty(authorizationToken);
        invalidToken(authorizationToken);
        return authorizationToken.replaceAll("Bearer ", "");
    }

    private void tokenEmpty(String authorizationToken){
        if(authorizationToken == null) {
            throw new IllegalStateException("토큰을 찾을 수 없습니다");
        }
    }

    private void invalidToken(String authorizationToken){
        if(!authorizationToken.contains("Bearer ")){
            throw new IllegalStateException("유효하지 않은 토큰입니다");
        }
    }

    private boolean expired(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
        // true 이면 만료된 토큰입니다
    }
}
