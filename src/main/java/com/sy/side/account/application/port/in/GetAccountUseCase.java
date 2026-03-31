package com.sy.side.account.application.port.in;

import com.sy.side.account.dto.response.AccountSelectResponse;
import java.util.List;

public interface GetAccountUseCase {
    List<AccountSelectResponse> findAllAccount(Long memberId);

    AccountSelectResponse findMyAccount(Long memberId, Long accountId);
}
