package com.konsulta.application.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
@Table(name = "kon_accounts")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Account extends AbstractEntity {
    //Variables - each account has a unique ID, email, password, name and surname

    @Email
    private String email;
    private String password;
    private String name;
    private String surname;

    //Getters and setters
    public String getEmail() {return email; }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getSurname(){return surname; }
    public void setSurname(String surname){this.surname = surname; }

    //To string
    public String toString() {
        return "Account{" +
                "email='" + this.getEmail() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                '}';
    }
}
