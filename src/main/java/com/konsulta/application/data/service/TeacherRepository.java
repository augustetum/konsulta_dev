package com.konsulta.application.data.service;
import com.konsulta.application.data.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeacherRepository
        extends
        JpaRepository<Teacher, Long>,
        JpaSpecificationExecutor<Teacher> {

}
