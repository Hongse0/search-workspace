package com.sy.side.account.application.port.out;

import com.sy.side.account.domain.Account;
import java.util.List;
import java.util.Optional;

public interface AccountQueryPort {
    boolean existsByMemberIdAndBrokerNameAndAccountNumber(
            Long memberId,
            String brokerName,
            String accountNumber
    );
    Optional<Account> findById(Long accountId);
    List<Account> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<Account> findByAccountIdAndMemberId(Long accountId, Long memberId);
}