package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Parent;
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

}

