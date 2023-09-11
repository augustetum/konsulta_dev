package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.repository.ParentRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentService {

    private final ParentRepository parentRepository;

    @Autowired
    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public Parent saveParent(Parent parent) {
        return parentRepository.save(parent);
    }

    public Parent findByEmail(String email) {
        return parentRepository.findByEmail(email);
    }

    public boolean isValidParentLogin(String email, String password) {
        Parent parent = parentRepository.findByEmail(email);
        return parent != null && parent.getPassword().equals(password);
    }
    public Parent initializeParent(Parent parent) {
        if (parent != null) {
            parent = parentRepository.findById(parent.getId()).orElse(null);
            if (parent != null && parent.getChildren() != null) {
                // Initialize the children collection if it's not already
                Hibernate.initialize(parent.getChildren());
            }
        }
        return parent;
    }


}

