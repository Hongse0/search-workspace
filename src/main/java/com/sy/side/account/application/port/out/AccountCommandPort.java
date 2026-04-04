package com.sy.side.account.application.port.out;

import com.sy.side.account.domain.Account;
import java.math.BigDecimal;

public interface AccountCommandPort {
    Account save(Account account);
    void delete(Account account);
    void withdrawCash(Long accountId, BigDecimal amount);
}