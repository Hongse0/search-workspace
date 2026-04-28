package com.sy.side.dashboard.application.port.out;

import java.util.List;
import java.util.Optional;

public interface DashboardAccountQueryPort {
    List<Long> findAllAccountIdsByMemberId(Long memberId);
}