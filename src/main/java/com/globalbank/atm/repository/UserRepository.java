package com.globalbank.atm.repository;

import com.globalbank.atm.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByCardNumber(String cardNumber);
    boolean existsByCardNumber(String cardNumber);
}
