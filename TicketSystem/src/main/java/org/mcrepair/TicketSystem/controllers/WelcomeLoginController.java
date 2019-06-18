package org.mcrepair.TicketSystem.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WelcomeLoginController {

    @RequestMapping(value="")
    public String index(){
        return "index";
    }

}
