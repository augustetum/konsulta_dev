package com.konsulta.application.data.repository;
import com.konsulta.application.data.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository
        extends
        JpaRepository<Account, Long>,
        JpaSpecificationExecutor<Account> {

}
