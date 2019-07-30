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



    private UserDao userDao;

    public CustomAuthenticationProvider(UserDao userDao){
        this.userDao = userDao;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        Object credentials = authentication.getCredentials();
        if (!(credentials instanceof String)) {
            return null;
        }

        String password = credentials.toString();

        List<User> myList =  userDao.findByEmail(name);
        if(myList.size() == 0){
            return null;
        }
        else{
            User user = myList.get(0);
            if(user.checkPasswordHash(password)){
                List<GrantedAuthority> grants = new ArrayList<>();
                grants.add(new SimpleGrantedAuthority(user.getRole()));
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
