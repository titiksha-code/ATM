package com.globalbank.atm.repository;

import com.globalbank.atm.entity.AtmSessionEntity;
import com.globalbank.atm.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtmSessionRepository extends JpaRepository<AtmSessionEntity, Long> {
    Optional<AtmSessionEntity> findFirstByCardNumberAndStatusOrderByLoginTimeDesc(String cardNumber, SessionStatus status);
    List<AtmSessionEntity> findTop20ByOrderByLoginTimeDesc();
    long countByStatus(SessionStatus status);
}
