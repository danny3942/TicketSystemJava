package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.mcrepair.TicketSystem.models.Status;
import org.mcrepair.TicketSystem.models.WorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.RolesAllowed;

import java.time.Month;
import java.util.GregorianCalendar;

import static org.mcrepair.TicketSystem.controllers.WorkRequestController.checkAuth;

@Controller
public class StatusAppointmentController {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkRequestDao workRequestDao;

    @RequestMapping(value="change-status/{id}")
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

    @RequestMapping(value="change-status/{id}" , method= RequestMethod.POST)
    public String changeStatus(Model model, @PathVariable int id, @RequestParam Status status) {
        checkAuth(model, userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")))){
            return "redirect:/";
        }
        WorkRequest wr = workRequestDao.findOne(id);
        if(status.equals(Status.UN_SCHEDULED)){
            wr.setAppointment(new GregorianCalendar());
        }
        wr.setStatus(status);

        workRequestDao.save(wr);
        return "redirect:/work/view-all-requests";
    }

    @RequestMapping(value="confirm/{id}")
    public String confirmStatus(Model model, @PathVariable int id){
        checkAuth(model, userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(workRequestDao.findOne(id).getUserEmail().equals(auth.getPrincipal().toString())) {
            model.addAttribute("workRequest", workRequestDao.findOne(id));
            model.addAttribute("page", 4);
            return "confirm";
        }
        return "redirect:/";
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

    @RequestMapping(value="appointment/{id}")
    public String viewAppointment(Model model, @PathVariable int id){
        checkAuth(model, userDao);
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
            model.addAttribute("workRequest", workRequestDao.findOne(id));
            model.addAttribute("months", Month.values());
            model.addAttribute(new GregorianCalendar());
            return "appointment-form";
        }
        return "redirect:/";
    }

    @RequestMapping(value="appointment/{id}" , method=RequestMethod.POST)
    public String processAppointment(Model model, @PathVariable int id, @RequestParam int month,
                                     @RequestParam int year, @RequestParam int day, @RequestParam int hour, @RequestParam int minute){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        checkAuth(model, userDao);
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))) {
                WorkRequest wr = workRequestDao.findOne(id);
                wr.setStatus(Status.SCHEDULED);
                wr.setAppointment(new GregorianCalendar(year, (month - 1), day, hour, minute));
                workRequestDao.save(wr);
                return "redirect:/work/view-all-requests";
            }
            else{
                return "redirect:/";
            }
        }
        else{
            return "redirect:/login";
        }
    }

    @RequestMapping(value="confirm-appointment/{id}")
    public String concreteAppointment(Model model, @PathVariable int id){
        checkAuth(model , userDao);
        model.addAttribute("workRequest",workRequestDao.findOne(id));
        return "confirm-appointment";
    }

    @RequestMapping(value="confirm-appointment/{id}" , method=RequestMethod.POST)
    public String confirmAppointment(Model model, @PathVariable int id, @RequestParam String aysure){
        checkAuth(model, userDao);
        WorkRequest wr = workRequestDao.findOne(id);
        if(aysure.equals("yes")){
            wr.setStatus(Status.IN_PROGRESS);
            workRequestDao.save(wr);
            return "redirect:/work/view/" + id;
        }
        else if (aysure.equals("no")){
            wr.setStatus(Status.UN_SCHEDULED);
            wr.setAppointment(new GregorianCalendar());
            workRequestDao.save(wr);
            return "redirect:/work/view/" + id;
        }
        else{
            return "redirect:/";
        }
    }

}
