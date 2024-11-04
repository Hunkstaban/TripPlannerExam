package dat.security.tokensecurity;

import com.nimbusds.jose.JOSEException;
import dat.security.exceptions.TokenCreationException;
import dat.security.dtos.UserDTO;

import java.text.ParseException;

public interface ITokenSecurity {
    UserDTO getUserWithRolesFromToken(String token) throws ParseException;
    boolean tokenIsValid(String token, String secret) throws ParseException, JOSEException;
    boolean tokenNotExpired(String token) throws ParseException;
    int timeToExpire(String token) throws ParseException;
    /**
     *
     * @param user a UserDTO object with username and a Set<String> of roles
     * @param ISSUER name of the issuer company or person
     * @param TOKEN_EXPIRE_TIME in milliseconds
     * @param SECRET_KEY 32 characters long
     * @return a JWT token
     * @throws TokenCreationException
     */
    String createToken(UserDTO user, String ISSUER, String TOKEN_EXPIRE_TIME, String SECRET_KEY) throws TokenCreationException;

}
