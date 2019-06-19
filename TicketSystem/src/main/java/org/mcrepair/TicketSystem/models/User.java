package org.mcrepair.TicketSystem.models;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
public class User {


    @NotNull
    @Email
    private String email;

    @Transient
    @NotNull(message="Passwords Must Match!")
    private String verify;

    @NotNull
    private String passwordHash;

    @Id
    @GeneratedValue
    private int id;

    public User(){
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
        return (hashPassword(passwordAttempt).equals(passwordHash));
    }

    private String hashPassword(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public String getPasswordHash() {
        return passwordHash;
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
