package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import static org.mcrepair.TicketSystem.controllers.WorkRequestController.checkAuth;

@Controller
public class WelcomeLoginController {

    @Autowired
    UserDao userDao;

    @RequestMapping(value="")
    public String index(Model model){
        model.addAttribute("title", "Moses Computer Repair");
        Authentication auth = checkAuth(model, userDao);
        //HOME PAGE
        // not too much interesting here....
        model.addAttribute("page", 0);
        return "welcome/index";
    }

    @RequestMapping("logout")
    public String logoutHandler() {
        //Logout handler.... again not too much interesting here
        SecurityContextHolder.clearContext();
        return "redirect:";
    }

    @RequestMapping(value="login")
    public String loginView(Model model){
        model.addAttribute("title", "Moses Computer Repair");
        //view login
        model.addAttribute("page", 2);
        return "welcome/login";
    }

    @RequestMapping(value="login" , method=RequestMethod.POST)
    public String loginProcess(Model model, @RequestParam String email){
        model.addAttribute("title", "Moses Computer Repair");
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        //See if the user logged in correctly
        if(auth.isAuthenticated()){
            //send them back to index.
            return "redirect:";
        }
        model.addAttribute("email", email);
        //refill the fields
        model.addAttribute("page", 2);
        return "welcome/login";
    }


    @RequestMapping(value="signup")
    public String signupView(Model model){
        model.addAttribute("title", "Moses Computer Repair");
        model.addAttribute(new User());
        //send the model a user object to use in setting up form
        model.addAttribute("page", 1);
        return "welcome/signup";
    }

    @RequestMapping(value="signup" , method=RequestMethod.POST)
    public String signupProccess(Model model, @ModelAttribute @Valid User user, Errors errors){

        model.addAttribute("title", "Moses Computer Repair");
        if(errors.hasErrors()) {
            model.addAttribute(user);
            //refill fields send back errors
            model.addAttribute("page", 1);
            return "welcome/signup";
        }
        else{
            //save new user and redirect.
            userDao.save(user);
            return "redirect:";
        }
    }

}
