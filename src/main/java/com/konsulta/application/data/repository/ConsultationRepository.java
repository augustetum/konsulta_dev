package com.konsulta.application.data.repository;
import com.konsulta.application.data.entity.Consultation;
import com.konsulta.application.data.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsultationRepository
        extends
        JpaRepository<Consultation, Long>,
        JpaSpecificationExecutor<Consultation> {

    boolean existsByParent(Parent parent);

}

