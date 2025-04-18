package com.konsulta.application.data.repository;
import com.konsulta.application.data.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentRepository
        extends
        JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student> {

}
