package com.sy.side.dashboard.controller;

import com.sy.side.common.annotation.UserParam;
import com.sy.side.common.entity.UserSession;
import com.sy.side.dashboard.application.port.in.GetDashboardHoldingsUseCase;
import com.sy.side.dashboard.dto.response.DashboardHoldingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final GetDashboardHoldingsUseCase getDashboardHoldingsUseCase;

    @GetMapping("/holdings")
    public DashboardHoldingsResponse getHoldings(
            @UserParam UserSession userSession
    ) {
        Long memberId = userSession.getMemberSession().getMemberId();
        return getDashboardHoldingsUseCase.getHoldings(memberId);
    }

//    @GetMapping("/holdings")
//    public DashboardHoldingsResponse getHoldings() {
//        Long memberId = 3L;
//        return getDashboardHoldingsUseCase.getHoldings(memberId);
//    }
}