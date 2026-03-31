package com.sy.side.account.application.port.out;

import com.sy.side.account.domain.Account;

public interface AccountCommandPort {
    Account save(Account account);
    void delete(Account account);
}