package com.sy.side.account.repository;

import com.sy.side.account.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberIdAndBrokerNameAndAccountNumber(
            Long memberId,
            String brokerName,
            String accountNumber
    );

    boolean existsByMemberIdAndAccountNumber(Long memberId, @NotBlank(message = "계좌번호는 필수입니다.") @Size(max = 50, message = "계좌번호는 50자를 넘을 수 없습니다.") String accountNumber);

    List<Account> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Account> findByAccountIdAndMemberId(Long accountId, Long memberId);

    // "멤버 + 증권사 + 계좌번호" 중복 방지
    boolean existsByMemberIdAndBrokerNameAndAccountNumber(Long memberId, String brokerName, String accountNumber);
}

