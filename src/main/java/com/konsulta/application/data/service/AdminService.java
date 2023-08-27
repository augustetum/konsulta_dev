package com.konsulta.application.data.service;

import com.konsulta.application.data.entity.Admin;
import com.konsulta.application.data.service.AdminRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) { this.repository = repository; }

    public Admin findByEmail(String email) { return repository.findByEmail(email); }

    public boolean isValidAdminLogin(String email, String password) {
        Admin admin = repository.findByEmail(email);
        return admin != null && admin.getPassword().equals(password);
    }

}