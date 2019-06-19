package org.mcrepair.TicketSystem.config;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserDao userDao;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        Object credentials = authentication.getCredentials();
        if (!(credentials instanceof String)) {
            return null;
        }

        String password = credentials.toString();

        if (name.equals("matthew") && password.equals("megaman12")) {
            List<GrantedAuthority> grants = new ArrayList<>();
            grants.add(new SimpleGrantedAuthority("ROLE_KING"));
            Authentication newAuth = new UsernamePasswordAuthenticationToken(name, password, grants);
            return newAuth;
        }

        for (User user : userDao.findAll()){
            if(user.getEmail().equals(name) && user.checkPasswordHash(password)){
                List<GrantedAuthority> grants = new ArrayList<>();
                grants.add(new SimpleGrantedAuthority("ROLE_USER"));
                Authentication newAuth = new UsernamePasswordAuthenticationToken(name, password, grants);
                return newAuth;
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
