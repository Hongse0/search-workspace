package com.sy.side.account.application.port.in;

public interface DeleteAccountUseCase {
    void deleteAccount(Long memberId, Long accountId);
}
