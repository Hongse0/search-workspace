package com.sy.side.tossinvest.application.port.in;

import com.sy.side.tossinvest.dto.response.TossInvestAccountResponse;

public interface GetTossInvestAccountsUseCase {

    TossInvestAccountResponse getAccounts();
}
