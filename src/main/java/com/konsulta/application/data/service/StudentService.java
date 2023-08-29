package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Student;
import com.konsulta.application.data.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Create or Update a Student
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    // Retrieve a Student by ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Retrieve all Students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Delete a Student by ID
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
