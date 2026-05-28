package com.sy.side.account.infrastructure.jpa;

import com.sy.side.account.domain.Account;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByMemberIdAndBrokerNameAndAccountNumber(
            Long memberId,
            String brokerName,
            String accountNumber
    );

    List<Account> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Account> findByAccountIdAndMemberId(Long accountId, Long memberId);

    List<Account> findAllByMemberIdOrderByAccountIdAsc(Long memberId);

    List<Account> findAllByActiveTrueOrderByAccountIdAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select a
        from Account a
        where a.accountId = :accountId
          and a.memberId = :memberId
          and a.active = true
    """)
    Optional<Account> findActiveAccountForUpdate(
            @Param("memberId") Long memberId,
            @Param("accountId") Long accountId
    );
}
