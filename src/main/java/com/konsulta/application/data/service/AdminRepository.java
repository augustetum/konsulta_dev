package com.konsulta.application.data.service;
import com.konsulta.application.data.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminRepository
        extends
        JpaRepository<Admin, Long>,
        JpaSpecificationExecutor<Admin> {
    Admin findByEmail(String email);

}
