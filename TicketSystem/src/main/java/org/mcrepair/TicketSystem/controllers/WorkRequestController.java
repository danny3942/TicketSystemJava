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

    public static void checkAuth(Model model, UserDao userDao){
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
    }

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
        model.addAttribute("page", 3);
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
                checkAuth(model, userDao);
                workRequest.setUserEmail(auth.getPrincipal().toString());
                workRequest.setStatus(Status.NEW);
                model.addAttribute("workRequest", workRequest);
            }
            model.addAttribute(workRequest);
            model.addAttribute("page", 3);
            return "request-form";
        }
        checkAuth(model, userDao);
        userDao.findByEmail(auth.getPrincipal().toString()).get(0).addWorkRequest(workRequest);
        workRequestDao.save(workRequest);

        return "redirect:/work/view/" + workRequest.getId();
    }

    @RequestMapping(value="view/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_KING"})
    public String viewRequest(Model model, @PathVariable int id){
        checkAuth(model, userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        WorkRequest wr = workRequestDao.findOne(id);
        if(wr.getStatus().equals(Status.COMPLETED) && wr.getUserEmail().equals(auth.getPrincipal().toString())){
            return "redirect:/work/confirm/" + id;
        }
        else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))){
            return "redirect:/work/change-status/" + id;
        }
        model.addAttribute("workRequest", workRequestDao.findOne(id));
        model.addAttribute("page", 4);
        return "view-request";
    }

    @RequestMapping(value="view-all/{id}")
    @RolesAllowed("ROLE_USER")
    public String viewMyRequests(Model model, @PathVariable int id) {
        checkAuth(model , userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(!(userDao.findOne(id).getEmail().equals(auth.getPrincipal().toString()))) {
            if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0)
                return "redirect:/work/view-all/" + userDao.findByEmail(auth.getPrincipal().toString()).get(0).getId();
            return "redirect:/";
        }
        model.addAttribute("page", 4);
        return "view-my-requests";
    }

    @RequestMapping(value="view-all-requests")
    @RolesAllowed("ROLE_KING")
    public String viewAllRequests(Model model){
        checkAuth(model ,userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")))){
            return "redirect:/";
        }
        model.addAttribute("users" , userDao);
        model.addAttribute("requests", workRequestDao.findAll());
        model.addAttribute("page", 5);
        return "view-all-requests";
    }

    @RequestMapping(value="change-status/{id}")
    @RolesAllowed("ROLE_KING")
    public String viewChangeStatus(Model model, @PathVariable int id){
        checkAuth(model , userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")))){
            return "redirect:/";
        }
        WorkRequest wr = workRequestDao.findOne(id);
        model.addAttribute("wr",wr);
        model.addAttribute("statuss", Status.values());
        model.addAttribute("page", 5);
        return "change-status";
    }

    @RequestMapping(value="change-status/{id}" , method=RequestMethod.POST)
    @RolesAllowed("ROLE_KING")
    public String changeStatus(Model model, @PathVariable int id, @RequestParam Status status) {
        checkAuth(model, userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")))){
            return "redirect:/";
        }
        WorkRequest wr = workRequestDao.findOne(id);
        wr.setStatus(status);

        workRequestDao.save(wr);
        return "redirect:/work/view-all-requests";
    }

    @RequestMapping(value="confirm/{id}")
    public String confirmStatus(Model model, @PathVariable int id){
        checkAuth(model, userDao);
        model.addAttribute("workRequest", workRequestDao.findOne(id));
        model.addAttribute("page", 4);
        return "confirm";
    }

    @RequestMapping(value="confirm/{id}" , method=RequestMethod.POST)
    public String processStatus(Model model, @PathVariable int id, @RequestParam String aysure){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        checkAuth(model, userDao);
        if (aysure.equals("yes")){
            workRequestDao.delete(id);
            return "redirect:/work/view-all/" + userDao.findByEmail(auth.getPrincipal().toString()).get(0).getId();
        }
        else if(aysure.equals("no")){
            WorkRequest wr = workRequestDao.findOne(id);
            wr.setStatus(Status.IN_PROGRESS);
            workRequestDao.save(wr);
            return "redirect:/work/view/" + id;
        }
        else{
            return "HALP";
        }
    }

}
