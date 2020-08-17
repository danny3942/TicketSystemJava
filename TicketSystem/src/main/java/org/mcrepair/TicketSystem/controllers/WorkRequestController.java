package org.mcrepair.TicketSystem.controllers;

import org.mcrepair.TicketSystem.data.UserDao;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.mcrepair.TicketSystem.models.Status;
import org.mcrepair.TicketSystem.models.User;
import org.mcrepair.TicketSystem.models.WorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Controller
@RequestMapping(value="work")
public class WorkRequestController {

    @Autowired
    UserDao userDao;

    @Autowired
    WorkRequestDao workRequestDao;

    private static ArrayList<WorkRequest> aps = new ArrayList<>();

    private static List<GregorianCalendar> availableTimes = new ArrayList<>();

    static Authentication checkAuth(Model model, UserDao userDao){
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        //used to get the auth object and pass needed navigation attributes easily
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0){
            model.addAttribute("user", userDao.findByEmail(auth.getPrincipal().toString()).get(0));
            model.addAttribute("foo" , true);
        }
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV"))
                || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ASSOC"))) {
            //used for admin specific navigation.
            model.addAttribute("bar", true);
        }
        return auth;
    }

    static void checkStatus(WorkRequestDao workRequestDao){
        for (WorkRequest wr : workRequestDao.findAll()){
            if(wr.getStatus().equals(Status.SCHEDULED)){
                if(wr.isToday()) {
                    wr.setStatus(Status.IN_PROGRESS);
                    workRequestDao.save(wr);
                }
            }
        }
    }

    static ArrayList<WorkRequest> getAppointments(User asc, WorkRequestDao workRequestDao){
        aps.clear();
        for (WorkRequest wr: workRequestDao.findAll()){
            if(wr.getAssociate() != null)
                if(wr.getAssociate().equals(asc.getEmail())){
                    aps.add(wr);
                }
        }
        return aps;
    }

    static List<GregorianCalendar> getAvailableTimes(UserDao userDao){
        loadAvailableTimes(userDao);
        return availableTimes;
    }

    static User getUserFromTime(GregorianCalendar timeToFind, UserDao userDao){
        for(User user: userDao.findAll()) {
            for (GregorianCalendar appointment: user.getAvailableTimes()){
                if(timeToFind.equals(appointment)){
                    return user;
                }
            }
        }
        return null;
    }

    static void removeAvailableTime(UserDao userDao, int timeId){
        getUserFromTime(availableTimes.get(timeId), userDao).removeAvailableTime(availableTimes.remove(timeId));
    }

    private static void loadAvailableTimes(UserDao userDao){
        availableTimes.clear();
        for(int i = 1; i <= userDao.count(); i++){
            for(GregorianCalendar appointment: userDao.findOne(i).getAvailableTimes())
                availableTimes.add(appointment);
        }
    }


    static void generateAvailableTimes(int day, int day1, int month, int month1, int userId, UserDao userDao){
        GregorianCalendar gc = new GregorianCalendar();
        if (gc.getTime().after(new GregorianCalendar(2019,month1,day1).getTime())){
            //see if they are specifying beyond this year.
            int year = 2020;
            month1 = 12;
            if(day == day1 && month < month1){
            int temp = day1;
            day1 = 31;
            //let the loop go through the whole month
            for(int y = 2019;y <= year;y++) {
                for (int i = month; i <= month1; i++) {
                    if (i == month1)
                        day1 = temp;
                    for (int j = day; j <= day1; j++) {
                        userDao.findOne(userId).addAvailableTime(new GregorianCalendar(y, i - 1, j, 12, 30));
                    }
                }
            }
            return;
            }
        }
        //check to see if the day is the same but not the month
        else if(day == day1 && month < month1){
            int temp = day1;
            day1 = 31;
            //let the loop go through the whole month
            for (int i = month; i <= month1; i++){
                if(i == month1)
                    //check to see if we hit the end of the month
                    day1 = temp;
                    //change to make it go to when specified
                for (int j = day; j <= day1; j++){
                    //add new available time for each day inside the time specified
                    userDao.findOne(userId).addAvailableTime(new GregorianCalendar(2019, i - 1, j, 12, 30));
                }
            }
            return;
        }
        else if(day == day1 && month==month1){
            //if the month and day are the same then they can only add one day.
            userDao.findOne(userId).addAvailableTime(new GregorianCalendar(2019, month-1,day,12,30));
            return;
        }
        for (int i = month; i <= month1; i++){
            for (int j = day; j <= day1; j++){
                //every other instance just loop through
                userDao.findOne(userId).addAvailableTime(new GregorianCalendar(2019, i - 1, j, 12, 30));
            }
        }
    }

    @RequestMapping(value="")
    public String viewMakeRequests(Model model){
        model.addAttribute("title", "MCR - Work Request");
        //Create new Request for the view
        WorkRequest workRequest = new WorkRequest();
        //Get authentication instance
        Authentication auth = checkAuth(model, userDao);
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
        model.addAttribute("page", 3);
        return "work/request-form";
    }

    @RequestMapping(value="" , method = RequestMethod.POST)
    public String makeRequests(Model model, @ModelAttribute @Valid WorkRequest workRequest, Errors errors){
        Authentication auth = checkAuth(model, userDao);
        //ensure their is a user to tie the work request to.
        if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0) {
            if (errors.hasErrors()) {
                //setup work request to be sent back to refill fields
                workRequest.setUserEmail(auth.getPrincipal().toString());
                workRequest.setStatus(Status.NEW);
                workRequest.setAppointment(new GregorianCalendar());
                model.addAttribute(workRequest);
                model.addAttribute("page", 3);
                return "work/request-form";
            }
            //find the user
            userDao.findByEmail(auth.getPrincipal().toString()).get(0).addWorkRequest(workRequest);
            //set appointment to blank
            workRequest.setAppointment(new GregorianCalendar());
            //save to database
            workRequestDao.save(workRequest);
            //redirect
            return "redirect:/work/view/" + workRequest.getId();
        }
        //redirect if no one is logged in
        return "redirect:/login";
    }

    @RequestMapping(value="view/{id}")
    public String viewRequest(Model model, @PathVariable int id){
        Authentication auth = checkAuth(model, userDao);
        WorkRequest wr = workRequestDao.findOne(id);
        //redirect users according to the status of the workRequest
        if(wr.getStatus().equals(Status.COMPLETED) && wr.getUserEmail().equals(auth.getPrincipal().toString())){
            return "redirect:/confirm/" + id;
        }
        else if((wr.getStatus().equals(Status.NEW) || wr.getStatus().equals(Status.UN_SCHEDULED)) && wr.getUserEmail().equals(auth.getPrincipal().toString())){
            return "redirect:/appointment/" + id;
        }
        model.addAttribute("workRequest", workRequestDao.findOne(id));
        model.addAttribute("page", 4);
        return "work/view-request";
    }

    @RequestMapping(value="view-all/{id}")
    public String viewMyRequests(Model model, @PathVariable int id) {
        Authentication auth = checkAuth(model, userDao);
        //check to make sure it is the correct user viewing
        if(!(userDao.findOne(id).getEmail().equals(auth.getPrincipal().toString()))) {
            if(userDao.findByEmail(auth.getPrincipal().toString()).size() > 0)
                return "redirect:/work/view-all/" + userDao.findByEmail(auth.getPrincipal().toString()).get(0).getId();
            return "redirect:/";
        }
        //pass correct attributes and return template
        model.addAttribute("page", 4);
        return "work/view-my-requests";
    }

    @RequestMapping(value="view-all-requests")
    public String viewAllRequests(Model model){
        Authentication auth = checkAuth(model ,userDao);
        //Check to make sure its an admin viewing
        if(!(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING"))
            || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV")))){
            return "redirect:/";
        }
        //pass correct attributes
        model.addAttribute("users" , userDao);
        model.addAttribute("requests", workRequestDao.findAll());
        model.addAttribute("page", 5);
        //Return template
        return "work/view-all-requests";
    }
}
