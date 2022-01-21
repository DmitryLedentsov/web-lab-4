package weblab4.sequrity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import weblab4.entitiesDTO.OwnerDTO;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(SecurityConstants.SIGN_IN_URL);
    }

    //this method is called when user trys to enter the app
    //here we create the user POJO object and then generate Authentication object
    // which spring security use to authenticate user
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            OwnerDTO owner = new ObjectMapper()
                    .readValue(req.getInputStream(), OwnerDTO.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            owner.getLogin(),
                            owner.getPassword(),
                            new ArrayList<>()) //this list is empty list of roles
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //this method is called if attemptAuthentication finished with success
    //here we check that user has proper login and password, and we need to return him an auth token
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        String token = JWT.create()
                .withSubject(((OwnerDTO) auth.getPrincipal()).getLogin())
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(SecurityConstants.SECRET.getBytes()));

        String body = ((OwnerDTO) auth.getPrincipal()).getLogin() + " " + token;
        res.getWriter().write(body);
        res.getWriter().flush();
    }
}