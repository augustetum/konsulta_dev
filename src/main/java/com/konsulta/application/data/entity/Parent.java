package com.konsulta.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.Set;

@Entity
@Table(name = "kon_parents")
public class Parent extends Account {
    private String phoneNumber;

    @ManyToMany
    private Set<Student> children; //parents can have multiple children

    public String getPhoneNumber() {return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}
    public Set<Student> getChildren() {return children;}
    public void setChildren(Set<Student> children) {this.children = children;}
}

