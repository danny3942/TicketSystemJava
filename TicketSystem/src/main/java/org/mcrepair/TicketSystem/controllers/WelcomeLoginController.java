package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class WelcomeLoginController {

    @Autowired
    UserDao userDao;

    @RequestMapping(value="")
    public String index(Model model){
        model.addAttribute("title", "Moses Computer Repair");
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)){
            if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0){
                model.addAttribute("foo", true);
                model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
                if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
                    model.addAttribute("bar", true);
                }
            }
        }
        return "index";
    }

    @RequestMapping("logout")
    public String logoutHandler() {
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

    @RequestMapping(value="login")
    public String loginView(Model model){
        model.addAttribute("title", "Moses Computer Repair");
        return "login";
    }

    @RequestMapping(value="login" , method=RequestMethod.POST)
    public String loginProcess(Model model, @RequestParam String email){
        model.addAttribute("title", "Moses Computer Repair");
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(auth.isAuthenticated()){
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
                model.addAttribute("bar", true);
            }
            model.addAttribute("foo" , true);
            model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
            return "index";
        }
        model.addAttribute("email", email);
        return "login";
    }


    @RequestMapping(value="signup")
    public String signupView(Model model){
        model.addAttribute("title", "Moses Computer Repair");
        model.addAttribute(new User());
        return "signup";
    }

    @RequestMapping(value="signup" , method=RequestMethod.POST)
    public String signupProccess(Model model, @ModelAttribute @Valid User user, Errors errors){

        model.addAttribute("title", "Moses Computer Repair");
        if(errors.hasErrors()) {
            model.addAttribute(user);
            return "signup";
        }
        else{
            userDao.save(user);
            return "redirect:";
        }
    }

}
