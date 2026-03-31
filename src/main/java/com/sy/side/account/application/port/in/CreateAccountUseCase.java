package com.sy.side.account.application.port.in;

import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.account.dto.response.AccountResponse;
import jakarta.validation.Valid;

public interface CreateAccountUseCase {
    AccountResponse createAccount(Long memberId, @Valid AccountCreateRequest request);
}
