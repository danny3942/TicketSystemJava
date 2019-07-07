package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.mcrepair.TicketSystem.controllers.WorkRequestController.checkAuth;

@Controller
public class ToDoController {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkRequestDao workRequestDao;


    @RequestMapping(value="todos")
    public String todoView(Model model){
        Authentication auth = checkAuth(model, userDao);
        return "todo/todos";
    }

}
