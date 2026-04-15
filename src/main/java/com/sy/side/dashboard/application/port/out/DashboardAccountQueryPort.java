package com.sy.side.dashboard.application.port.out;

import java.util.Optional;

public interface DashboardAccountQueryPort {
    Optional<Long> findPrimaryAccountIdByMemberId(Long memberId);
}