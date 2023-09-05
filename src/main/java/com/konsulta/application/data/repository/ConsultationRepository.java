package com.konsulta.application.data.repository;
import com.konsulta.application.data.entity.Consultation;
import com.konsulta.application.data.entity.Parent;
import com.konsulta.application.data.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ConsultationRepository
        extends
        JpaRepository<Consultation, Long>,
        JpaSpecificationExecutor<Consultation> {

    boolean existsByParent(Parent parent);

    List<Consultation> findByTeacher(Teacher teacher);
}

