package com.globalbank.atm.repository;

import com.globalbank.atm.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    Optional<AccountEntity> findByAccountNo(String accountNo);
    Optional<AccountEntity> findByUser_UserId(Long userId);
    Optional<AccountEntity> findByUser_CardNumber(String cardNumber);
}
