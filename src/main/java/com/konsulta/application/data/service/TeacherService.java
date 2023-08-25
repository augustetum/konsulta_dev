package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeacherService {

    private final TeacherRepository repository;

    public TeacherService(TeacherRepository repository) {
        this.repository = repository;
    }

    public Optional<Teacher> get(Long id) {
        return repository.findById(id);
    }

    public Teacher update(Teacher entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Teacher> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Teacher> list(Pageable pageable, Specification<Teacher> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }
}




