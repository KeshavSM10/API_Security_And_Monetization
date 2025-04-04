package authentication;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWTUtil {
	
	private static final String Signing_Key = "Security key";
	
	public String GenerateToken(String username) {
			
		return Jwts.builder()
				.setSubject(username) // modifying username
				.setIssuedAt(new java.util.Date()) //getting date of issuing
				.setExpiration(new java.util.Date(System.currentTimeMillis()+1000*60*60)) //setting expiry of token
				.signWith(SignatureAlgorithm.HS256, Signing_Key) //signing the username for auth
				.compact(); //compacting all in one
	}
	
	public String ExtractUsername(String token) {
		return extractClaim(token, Claims::getSubject); //method to get username out of token
	}
	
	public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); //extract expiry out of token
    }

	// To extract claims that is data inside token.
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { //<T> means t can be any type
		
		JwtParser jwtparser = Jwts.parser()
				.setSigningKey(Signing_Key)
				.build();
		
		Claims claims = jwtparser.parseClaimsJws(token).getBody();
		
		return claimsResolver.apply(claims);		
	}
	
	//checking if is token expired
	public boolean isTokenExpired(String token) {
		
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}
	
	//checking if token valid with respect to username
	public boolean validateToken(String Token, String username) {
		
		final String extractUsername = ExtractUsername(Token);
		return (username.equals(extractUsername) && !isTokenExpired(Token));
	}
}
