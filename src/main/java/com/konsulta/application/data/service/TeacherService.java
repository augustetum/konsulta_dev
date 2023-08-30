package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Teacher;
import com.konsulta.application.data.entity.Timeslot;
import com.konsulta.application.data.repository.TeacherRepository;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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

    public Teacher findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public int count() {
        return (int) repository.count();
    }

    public boolean isValidTeacherLogin(String email, String password) {
        Teacher teacher = repository.findByEmail(email);
        return teacher != null && teacher.getPassword().equals(password);
    }

    public void addTimeslotsToTeacher(Long teacherId, List<Timeslot> timeslots) {
        Optional<Teacher> teacherOptional = repository.findById(teacherId);
        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();
            // Add the new timeslots to the existing list
            teacher.getTimeslots().addAll(timeslots);
            repository.save(teacher);
        }
    }

}




