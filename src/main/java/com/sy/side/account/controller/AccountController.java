package com.sy.side.account.controller;

import com.sy.side.account.application.port.in.CreateAccountUseCase;
import com.sy.side.account.application.port.in.DeleteAccountUseCase;
import com.sy.side.account.application.port.in.GetAccountUseCase;
import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.account.dto.response.AccountResponse;
import com.sy.side.account.dto.response.AccountSelectResponse;
import com.sy.side.common.annotation.UserParam;
import com.sy.side.common.entity.UserSession;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final DeleteAccountUseCase deleteAccountUseCase;

    /** 계좌 등록 */
    @PostMapping("/register")
    public AccountResponse createAccount(
            @UserParam UserSession userSession,
            @Valid @RequestBody AccountCreateRequest request
    ) {
        Long memberId = userSession.getMemberSession().getMemberId();
        log.info("[Account] createAccount memberId={}, request={}", memberId, request);

        return createAccountUseCase.createAccount(memberId, request);
    }

    /** 계좌 전체 조회 */
    @PostMapping("/select/all")
    public List<AccountSelectResponse> selectAllAccount(@UserParam UserSession userSession) {
        Long memberId = userSession.getMemberSession().getMemberId();
        return getAccountUseCase.findAllAccount(memberId);
    }

    /** 계좌 전체 삭제 */
    @DeleteMapping("/{accountId}")
    public void deleteAccount(
            @UserParam UserSession userSession,
            @PathVariable Long accountId
    ) {
        Long memberId = userSession.getMemberSession().getMemberId();
        deleteAccountUseCase.deleteAccount(memberId, accountId);
    }
}