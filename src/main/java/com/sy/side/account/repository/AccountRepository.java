package com.sy.side.account.repository;

import com.sy.side.account.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberIdAndBrokerNameAndAccountNumber(
            Long memberId,
            String brokerName,
            String accountNumber
    );

    boolean existsByMemberIdAndAccountNumber(Long memberId, @NotBlank(message = "계좌번호는 필수입니다.") @Size(max = 50, message = "계좌번호는 50자를 넘을 수 없습니다.") String accountNumber);
}

