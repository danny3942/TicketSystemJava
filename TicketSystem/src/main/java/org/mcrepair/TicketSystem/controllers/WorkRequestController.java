package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.mcrepair.TicketSystem.models.Status;
import org.mcrepair.TicketSystem.models.WorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.GregorianCalendar;

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
        else{
            return;
        }
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
            model.addAttribute("bar", true);
        }
    }

    @RequestMapping(value="")
    public String viewMakeRequests(Model model){
        model.addAttribute("title", "MCR - Work Request");
        //Create new Request for the view
        WorkRequest workRequest = new WorkRequest();
        //Get authentication instance
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
                workRequest.setUserEmail(auth.getPrincipal().toString());
                model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
                workRequest.setStatus(Status.NEW);
                workRequest.setAppointment(new GregorianCalendar());
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
    public String makeRequests(Model model, @ModelAttribute @Valid WorkRequest workRequest, Errors errors){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        checkAuth(model, userDao);
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
            if (errors.hasErrors()) {
                checkAuth(model, userDao);
                workRequest.setUserEmail(auth.getPrincipal().toString());
                workRequest.setStatus(Status.NEW);
                workRequest.setAppointment(new GregorianCalendar());
                model.addAttribute(workRequest);
                model.addAttribute("page", 3);
                return "request-form";
            }
            userDao.findByEmail(auth.getPrincipal().toString()).get(0).addWorkRequest(workRequest);
            workRequest.setAppointment(new GregorianCalendar());
            workRequestDao.save(workRequest);
            return "redirect:/work/view/" + workRequest.getId();
        }
        return "redirect:/login";
    }

    @RequestMapping(value="view/{id}")
    public String viewRequest(Model model, @PathVariable int id){
        checkAuth(model, userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        WorkRequest wr = workRequestDao.findOne(id);
        if(wr.getStatus().equals(Status.COMPLETED) && wr.getUserEmail().equals(auth.getPrincipal().toString())){
            return "redirect:/confirm/" + id;
        }
        else if(wr.getStatus().equals(Status.UN_SCHEDULED) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))){
            return "redirect:/appointment/" + id;
        }
        else if(wr.getStatus().equals(Status.SCHEDULED) && wr.getUserEmail().equals(auth.getPrincipal().toString())){
            return "redirect:/confirm-appointment/" + id;
        }
        else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))){
            return "redirect:/change-status/" + id;
        }
        model.addAttribute("workRequest", workRequestDao.findOne(id));
        model.addAttribute("page", 4);
        return "view-request";
    }

    @RequestMapping(value="view-all/{id}")
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
}
