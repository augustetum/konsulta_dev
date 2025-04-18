package com.konsulta.application.data.repository;
import com.konsulta.application.data.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ParentRepository
        extends
        JpaRepository<Parent, Long>,
        JpaSpecificationExecutor<Parent> {
    Parent findByEmail(String email);
}
