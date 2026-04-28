package com.sy.side.account.infrastructure.jpa;

import com.sy.side.account.domain.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByMemberIdAndBrokerNameAndAccountNumber(
            Long memberId,
            String brokerName,
            String accountNumber
    );

    List<Account> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Account> findByAccountIdAndMemberId(Long accountId, Long memberId);

    List<Account> findAllByMemberIdOrderByAccountIdAsc(Long memberId);
}