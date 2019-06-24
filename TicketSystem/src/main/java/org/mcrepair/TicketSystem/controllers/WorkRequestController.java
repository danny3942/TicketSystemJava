package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.mcrepair.TicketSystem.models.Status;
import org.mcrepair.TicketSystem.models.User;
import org.mcrepair.TicketSystem.models.WorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Controller
@RequestMapping(value="work")
public class WorkRequestController {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkRequestDao workRequestDao;

    @RequestMapping(value="")
    @RolesAllowed({"ROLE_USER" , "ROLE_KING"})
    public String viewMakeRequests(Model model){
        model.addAttribute("title", "MCR - Work Request");
        WorkRequest workRequest = new WorkRequest();
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
                workRequest.setUserEmail(auth.getPrincipal().toString());
                model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
                workRequest.setStatus(Status.NEW);
                model.addAttribute("workRequest", workRequest);
                model.addAttribute("foo" , true);
                if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
                    model.addAttribute("bar", true);
                }
            }
        }
        return "request-form";
    }

    @RequestMapping(value="" , method = RequestMethod.POST)
    @RolesAllowed({"ROLE_USER" , "ROLE_KING"})
    public String makeRequests(Model model, @ModelAttribute @Valid WorkRequest workRequest, Errors errors){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(errors.hasErrors()){
            if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
                workRequest.setUserEmail(auth.getPrincipal().toString());
                model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
                workRequest.setStatus(Status.NEW);
                model.addAttribute("workRequest", workRequest);
                model.addAttribute("foo" , true);
            }
            model.addAttribute(workRequest);
            return "request-form";
        }
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
            model.addAttribute("bar", true);
        }
        model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
        userDao.findByEmail(auth.getPrincipal().toString()).get(0).addWorkRequest(workRequest);
        workRequestDao.save(workRequest);

        return "redirect:/work/view/" + workRequest.getId();
    }

    @RequestMapping(value="view/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_KING"})
    public String viewRequest(Model model, @PathVariable int id){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0){
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
                model.addAttribute("bar", true);
            }
            model.addAttribute("foo" , true);
            model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
        }
        model.addAttribute("workRequest", workRequestDao.findOne(id));
        return "view-request";
    }

    @RequestMapping(value="view-all/{id}")
    @RolesAllowed("ROLE_USER")
    public String viewMyRequests(Model model, @PathVariable int id){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
            model.addAttribute("bar", true);
        }
        model.addAttribute("poop", Status.COMPLETED);
        model.addAttribute("foo" , true);
        model.addAttribute("user", userDao.findOne(id));
        return "view-my-requests";
    }

    @RequestMapping(value="view-all-requests")
    @RolesAllowed("ROLE_KING")
    public String viewAllRequests(Model model){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0){
            model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
            model.addAttribute("foo" , true);
        }
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
            model.addAttribute("bar", true);
        }
        model.addAttribute("users" , userDao);
        model.addAttribute("requests", workRequestDao.findAll());
        return "view-all-requests";
    }

    @RequestMapping(value="change-status/{id}")
    @RolesAllowed("ROLE_KING")
    public String viewChangeStatus(Model model, @PathVariable int id){
        WorkRequest wr = workRequestDao.findOne(id);
        model.addAttribute("wr",wr);
        model.addAttribute("statuss", Status.values());
        return "change-status";
    }

    @RequestMapping(value="change-status/{id}" , method=RequestMethod.POST)
    @RolesAllowed("ROLE_KING")
    public String changeStatus(Model model, @PathVariable int id, @RequestParam Status status) {
        WorkRequest wr = workRequestDao.findOne(id);
        wr.setStatus(status);

        workRequestDao.save(wr);
        return "redirect:/work/view-all-requests";
    }

}
