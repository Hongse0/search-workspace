package com.sy.side.account.controller;


import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.account.dto.response.AccountResponse;
import com.sy.side.account.dto.response.AccountSelectResponse;
import com.sy.side.account.service.AccountService;
import com.sy.side.common.annotation.UserParam;
import com.sy.side.common.entity.UserSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    /**
     * 계좌 등록
     * POST /v1/account
     */
    @PostMapping("/register")
    public AccountResponse createAccount(
            @UserParam UserSession userSession,
            @Valid @RequestBody AccountCreateRequest request,
            HttpServletRequest httpServletRequest
    ) {
        log.info("[Account] headers X-MEMBER-ID={}, X-USER-TYPE={}",
                httpServletRequest.getHeader("X-MEMBER-ID"),
                httpServletRequest.getHeader("X-USER-TYPE"));
        Long memberId = userSession.getMemberSession().getMemberId();
        log.info("[Account] createAccount memberId={}, request={}", memberId, request);

        return accountService.createAccount(memberId, request);
    }

    /**
     * 계좌 목록 조회
     * POST /v1/account
     */
    @PostMapping("/select/all")
    public List<AccountSelectResponse> selectAllAccount(@UserParam UserSession userSession){
        Long memberId = userSession.getMemberSession().getMemberId();

        return accountService.findAllAccount(memberId);
    }

    /**
     * 내 계좌 삭제
     * DELETE /v1/account/{accountId}
     */
    @DeleteMapping("/{accountId}")
    public void deleteAccount(
            @UserParam UserSession userSession,
            @PathVariable Long accountId
    ) {
        Long memberId = userSession.getMemberSession().getMemberId();
        accountService.deleteAccount(memberId, accountId);
    }

}
