package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.repository.ParentRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class ParentService {

    private final ParentRepository parentRepository;

    @Autowired
    public ParentService(ParentRepository parentRepository) {
        this.parentRepository = parentRepository;
    }

    public Page<Parent> list(Pageable pageable) {
        return parentRepository.findAll(pageable);
    }

    public Optional<Parent> get(Long id) {
        return parentRepository.findById(id);
    }

    public Parent update(Parent entity) {
        return parentRepository.save(entity);
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

    //generates a confirmation email for the parent
    public void sendConfirmationEmailToParent(Parent parent, Teacher selectedTeacher, String scheduledTime) {
        String parentEmail = parent.getEmail();
        String parentSubject = "Consultation Confirmation";
        String parentContent = "Your consultation with " + selectedTeacher.getName() + " is scheduled for " + scheduledTime;

        try {
            EmailSender.sendEmail(parentEmail, parentSubject, parentContent);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle email sending errors
        }
    }

}

