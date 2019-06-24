package org.mcrepair.TicketSystem.models;

import org.hibernate.validator.constraints.Email;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class WorkRequest {

    @NotNull
    @Size(min=8)
    private String serialNumber;

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String typeOfComputer;

    @ManyToOne
    private User user;

    @NotNull
    private Status status;

    @NotNull
    @Email
    private String userEmail;

    @NotNull
    private String opSys;

    @NotNull
    private String problem;

    private String description;

    public WorkRequest() {
    }

    public WorkRequest(String serialNumber, String typeOfComputer, User user, Status status, String userEmail, String opSys, String problem, String description) {
        this.serialNumber = serialNumber;
        this.typeOfComputer = typeOfComputer;
        this.user = user;
        this.status = status;
        this.userEmail = userEmail;
        this.opSys = opSys;
        this.problem = problem;
        this.description = description;
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
        this.serialNumber = serialNumber;
    }

    public int getId() {
        return id;
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
