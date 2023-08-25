package com.konsulta.application.data.service;
import com.konsulta.application.data.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsultationRepository
        extends
        JpaRepository<Consultation, Long>,
        JpaSpecificationExecutor<Consultation> {

}

