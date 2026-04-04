package com.sy.side.account.infrastructure;

import com.sy.side.account.application.port.out.AccountCommandPort;
import com.sy.side.account.application.port.out.AccountQueryPort;
import com.sy.side.account.domain.Account;
import com.sy.side.account.infrastructure.jpa.AccountRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountAdapter implements AccountCommandPort, AccountQueryPort {

    private final AccountRepository accountRepository;

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void delete(Account account) {
        accountRepository.delete(account);
    }

    @Override
    public void withdrawCash(Long accountId, BigDecimal amount) {

    }

    @Override
    public boolean existsByMemberIdAndBrokerNameAndAccountNumber(
            Long memberId,
            String brokerName,
            String accountNumber
    ) {
        return accountRepository.existsByMemberIdAndBrokerNameAndAccountNumber(
                memberId,
                brokerName,
                accountNumber
        );
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public List<Account> findAllByMemberIdOrderByCreatedAtDesc(Long memberId) {
        return accountRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Override
    public Optional<Account> findByAccountIdAndMemberId(Long accountId, Long memberId) {
        return accountRepository.findByAccountIdAndMemberId(accountId, memberId);
    }
}