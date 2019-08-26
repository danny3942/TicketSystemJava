package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.mcrepair.TicketSystem.models.Status;
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


import java.time.Month;
import java.util.GregorianCalendar;

import static org.mcrepair.TicketSystem.controllers.WorkRequestController.*;

@Controller
public class StatusAppointmentController {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkRequestDao workRequestDao;

    @RequestMapping(value="change-status/{id}")
    public String viewChangeStatus(Model model, @PathVariable int id){
        Authentication auth = checkAuth(model, userDao);
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")))){
            return "redirect:/";
        }
        //TO BE DELETED
        //AUTOMATIC STATUS CHANGES TO BE IMPLEMENTED.
        WorkRequest wr = workRequestDao.findOne(id);
        model.addAttribute("wr",wr);
        model.addAttribute("statuss", Status.values());
        model.addAttribute("page", 5);
        return "status/change-status";
    }

    @RequestMapping(value="change-status/{id}" , method= RequestMethod.POST)
    public String changeStatus(Model model, @PathVariable int id, @RequestParam Status status) {
        Authentication auth = checkAuth(model, userDao);
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")))){
            return "redirect:/";
        }
        WorkRequest wr = workRequestDao.findOne(id);
        if(status.equals(Status.UN_SCHEDULED)){
            wr.setAppointment(new GregorianCalendar());
        }
        //USED FOR TESING PURPOSES
        //AUTOMATIC STATUS CHANGES TO BE IMPLEMENTED
        wr.setStatus(status);

        workRequestDao.save(wr);
        return "redirect:/work/view-all-requests";
    }

    @RequestMapping(value="confirm/{id}")
    public String confirmStatus(Model model, @PathVariable int id){
        Authentication auth = checkAuth(model, userDao);
        if(workRequestDao.findOne(id).getUserEmail().equals(auth.getPrincipal().toString())) {
            //Pass the specified work request
            //Allows the user confirm if the work was completed
            model.addAttribute("workRequest", workRequestDao.findOne(id));
            model.addAttribute("page", 4);
            return "status/confirm";
        }
        return "redirect:/";
    }

    @RequestMapping(value="confirm/{id}" , method=RequestMethod.POST)
    public String processStatus(Model model, @PathVariable int id, @RequestParam String aysure){
        Authentication auth = checkAuth(model, userDao);
        if (aysure.equals("yes")){
            //if work was completed
            // Delete work request
            workRequestDao.delete(id);

            return "redirect:/work/view-all/" + userDao.findByEmail(auth.getPrincipal().toString()).get(0).getId();
        }
        else if(aysure.equals("no")){
            //if confirmation fails
            // change status back to scheduled
            WorkRequest wr = workRequestDao.findOne(id);
            wr.setStatus(Status.SCHEDULED);
            checkStatus(workRequestDao);
            return "redirect:/work/view/" + id;
        }
        else{
            return "HALP";
        }
    }

    @RequestMapping(value="appointment/{id}")
    public String viewAppointment(Model model, @PathVariable int id){
        Authentication auth = checkAuth(model, userDao);
        //get available times.
            model.addAttribute("times",getAvailableTimes(userDao));
            //pass workrequest dao
            model.addAttribute("workRequest", workRequestDao.findOne(id));
        return "status/appointment-form";
    }

    @RequestMapping(value="appointment/{id}" , method=RequestMethod.POST)
    public String processAppointment(Model model, @PathVariable int id, @RequestParam int time){
        Authentication auth = checkAuth(model, userDao);
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
            WorkRequest wr = workRequestDao.findOne(id);
            User user = getUserFromTime(getAvailableTimes(userDao).get(time), userDao);
            wr.setStatus(Status.SCHEDULED);
            //set status to scheduled and pull the specified time out of the list.
            wr.setAppointment(getAvailableTimes(userDao).get(time));
            wr.setAssociate(user.getEmail());
            removeAvailableTime(userDao, time);
            //update
            userDao.save(user);
            workRequestDao.save(wr);
            return "redirect:/work/view-all/" + userDao.findByEmail(auth.getPrincipal().toString()).get(0).getId();
        }
        else{
            return "redirect:/login";
        }
    }

    @RequestMapping(value="time-set/{id}")
    public String setTimes(@PathVariable int id,Model model){
        Authentication auth = checkAuth(model, userDao);
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ASSOC"))) {
            //Allows admins to specify when they are available to take appointments.
            model.addAttribute("months", Month.values());
            return "status/time-set";
        }
        else{return "redirect:/";}
    }

    @RequestMapping(value="time-set/{id}", method=RequestMethod.POST)
    public String setTimesBoi(@PathVariable int id,Model model, @RequestParam int day, @RequestParam int month,
                              @RequestParam int day1, @RequestParam int month1){
        Authentication auth = checkAuth(model, userDao);
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ASSOC"))) {
            //generates available appointment times based on the times they have specified
            generateAvailableTimes(day, day1, month, month1, id, userDao);
            userDao.save(userDao.findOne(id));
            return "redirect:/work/view-all-requests";
        }
        return "redirect:/";
    }
}
