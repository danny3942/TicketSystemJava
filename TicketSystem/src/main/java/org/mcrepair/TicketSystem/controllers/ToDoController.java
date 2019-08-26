package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.mcrepair.TicketSystem.models.User;
import org.mcrepair.TicketSystem.models.WorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import static org.mcrepair.TicketSystem.controllers.WorkRequestController.checkAuth;
import static org.mcrepair.TicketSystem.controllers.WorkRequestController.getAppointments;

@Controller
public class ToDoController {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkRequestDao workRequestDao;


    @RequestMapping(value="todos")
    public String todoView(Model model){
        Authentication auth = checkAuth(model, userDao);
        for(WorkRequest wr: workRequestDao.findAll()){
            if(auth.getPrincipal().toString().equals(wr.getAssociate())){
                model.addAttribute("weInThisBoi", true);
            }
        }
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
    @RequestMapping(value="change-role/{id}", method= RequestMethod.POST)
    public String changeRoleProcess(Model model, @PathVariable int id, @RequestParam String role){
        Authentication auth = checkAuth(model, userDao);
        User user = userDao.findOne(id);
        user.setRole(auth , role);
        userDao.save(user);
        return "redirect:/permiss";
    }
    @RequestMapping(value="my-appointments")
    public String viewMyAppointments(Model model){
        Authentication auth = checkAuth(model ,userDao);
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV")))){
            return "redirect:/";
        }
        User asc = userDao.findByEmail(auth.getPrincipal().toString()).get(0);
        //pass correct attributes
        ArrayList<WorkRequest> aps = new ArrayList<>(getAppointments(asc, workRequestDao));
        model.addAttribute("requests",aps);
        //Return template
        return "todo/my-appointments";
    }

}
