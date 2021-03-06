package org.mcrepair.TicketSystem.models;

import org.hibernate.validator.constraints.Email;
import org.mcrepair.TicketSystem.data.WorkRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Component
@Entity
public class WorkRequest {

    @NotNull(message="There is already a request for this computer")
    @Size(min=8 , message="Invalid serial number")
    private String serialNumber;

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String typeOfComputer;

    @ManyToOne
    private User user;

    private GregorianCalendar appointment;

    @NotNull
    private Status status;

    private String associate;

    @NotNull
    @Email
    private String userEmail;

    @NotNull
    private String opSys;

    @NotNull
    private String problem;

    @NotNull
    private String description;

    @Transient
    private static WorkRequestDao workRequestDao;

    public WorkRequest() {
    }

    public WorkRequest(String serialNumber, GregorianCalendar appointment, String typeOfComputer, User user, Status status, String userEmail, String opSys, String problem, String description) {
        this.serialNumber = serialNumber;
        this.typeOfComputer = typeOfComputer;
        this.user = user;
        this.status = status;
        this.userEmail = userEmail;
        this.opSys = opSys;
        this.problem = problem;
        this.description = description;
        this.appointment = appointment;
    }

    public String getAssociate() {
        return associate;
    }

    public void setAssociate(String associate) {
        this.associate = associate;
    }

    public boolean isScheduled(){
        return (status.equals(Status.SCHEDULED) || status.equals(Status.IN_PROGRESS));
    }

    public Calendar getAppointment() {
        return appointment;
    }

    public void setAppointment(GregorianCalendar appointment) {
        this.appointment = appointment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setSerialNumber(String serialNumber) {
        for(WorkRequest wr : workRequestDao.findAll()){
           if(wr.getSerialNumber().equals(serialNumber)){
                this.serialNumber = null;
                return;
           }
        }
        this.serialNumber = serialNumber;
    }

    public int getId() {
        return id;
    }

    @Autowired
    public void setWorkRequestDao(WorkRequestDao workRequestDao1) {
        workRequestDao = workRequestDao1;
    }

    public String getTypeOfComputer() {
        return typeOfComputer;
    }

    public void setTypeOfComputer(String typeOfComputer) {
        this.typeOfComputer = typeOfComputer;
    }

    public String getOpSys() {
        return opSys;
    }

    public void setOpSys(String opSys) {
        this.opSys = opSys;
    }

    public String getProblem() {
        return problem;
    }

    public boolean isToday(){
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(9,1);
        return (appointment.after(new GregorianCalendar()) && appointment.before(gc));
    }

    public boolean doesntMakeSense(){
        if (status.equals(Status.NEW) || status.equals(Status.UN_SCHEDULED)) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.add(9, -3);
            return appointment.before(new GregorianCalendar());
        }
        return true;
    }

    public boolean isDone(){
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(9,-2);
        return appointment.before(new GregorianCalendar()) && status.equals(Status.IN_PROGRESS);
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
