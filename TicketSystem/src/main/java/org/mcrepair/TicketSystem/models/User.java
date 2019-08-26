package org.mcrepair.TicketSystem.models;

import org.hibernate.jdbc.Work;
import org.hibernate.validator.constraints.Email;
import org.mcrepair.TicketSystem.data.UserDao;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@Entity
public class User {

    @Transient
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @NotNull
    @Email
    private String email;

    @Transient
    @NotNull(message="Passwords Must Match!")
    private String verify;

    @NotNull
    private String passwordHash;

    @NotNull
    private String role;

    @ElementCollection
    private List<GregorianCalendar> availableTimes;

    @OneToMany
    @JoinColumn(name = "workRequest_id")
    private List<WorkRequest> workRequests;

    @Id
    @GeneratedValue
    private int id;

    public User(){
        this.role = "ROLE_USER";
    }

    public boolean anyScheduled(){
        for(WorkRequest wr : workRequests) {
            if(wr.isScheduled()){
                return true;
            }
        }
        return false;
    }

    public boolean anyCompleted(){
        for(WorkRequest wr : workRequests) {
            if(wr.getStatus().equals(Status.COMPLETED)){
                return true;
            }
        }
        return false;
    }

    public String getRole() {
        return role;
    }

    public void setRole(){
        if(this.email.equals("mattmoses@gmail.com"))
            this.role = "ROLE_KING";
        else
            role = "ROLE_USER";
        verify = "";
    }

    public void setRole(Authentication auth,String role) {
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_KING")) ||
                auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV"))) {
            if (this.email.equals("mattmoses@gmail.com")) {
                this.role = "ROLE_KING";
            }
            else if (this.email.equals("danny3942.ds@gmail.com")) {
                this.role = "ROLE_DEV";
            }
            else {
                this.role = role;
            }
        }
        verify = "";
    }

    public void removeAvailableTime(GregorianCalendar time){availableTimes.remove(time);}

    public void addAvailableTime(GregorianCalendar time){
        availableTimes.add(time);
    }

    public List<GregorianCalendar> getAvailableTimes() {
        return availableTimes;
    }

    public void setAvailableTimes(List<GregorianCalendar> availableTimes) {
        this.availableTimes = availableTimes;
    }

    public void addWorkRequest(WorkRequest wr){
        workRequests.add(wr);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public boolean checkPasswordHash(String passwordAttempt){
        return passwordEncoder.matches(passwordAttempt,passwordHash);
    }
    private String hashPassword(String password){
        return passwordEncoder.encode(password);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<WorkRequest> getWorkRequests() {
        return workRequests;
    }

    public void setWorkRequests(List<WorkRequest> workRequests) {
        this.workRequests = workRequests;
    }

    public void setPasswordHash(String password) {
        this.passwordHash = hashPassword(password);
    }

    public int getId() {
        return id;
    }

    public User(String email, String verify, String password) {
        this();
        this.email = email;
        this.verify = verify;
        setPasswordHash(password);
    }
}
