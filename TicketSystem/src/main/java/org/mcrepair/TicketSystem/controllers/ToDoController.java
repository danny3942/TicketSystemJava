package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

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

    @RequestMapping(value="permiss")
    public String userPermissions(Model model){
        Authentication auth = checkAuth(model, userDao);
        model.addAttribute("users",userDao.findAll());
        return "todo/change-permiss";
    }


    @RequestMapping(value="change-role/{id}")
    public String changeRole(Model model, @PathVariable int id){
        Authentication auth = checkAuth(model, userDao);
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_KING");roles.add("ROLE_USER");roles.add("ROLE_DEV");roles.add("ROLE_ASSOC");
        model.addAttribute("currUser",userDao.findOne(id));
        model.addAttribute("roles",roles);
        return "todo/change-role";
    }
}
