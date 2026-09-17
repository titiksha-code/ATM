package com.globalbank.atm.repository;

import com.globalbank.atm.entity.TransactionEntity;
import com.globalbank.atm.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findTop8ByAccount_AccountIdOrderByTxnDateDesc(Long accountId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId " +
           "AND (:txnType IS NULL OR t.txnType = :txnType) " +
           "AND (:startDate IS NULL OR t.txnDate >= :startDate) " +
           "AND (:endDate IS NULL OR t.txnDate <= :endDate) " +
           "ORDER BY t.txnDate DESC")
    Page<TransactionEntity> findTransactionsFiltered(
            @Param("accountId") Long accountId,
            @Param("txnType") TransactionType txnType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.accountId = :accountId ORDER BY t.txnDate DESC")
    List<TransactionEntity> findAllByAccountIdOrderByTxnDateDesc(@Param("accountId") Long accountId);
}
