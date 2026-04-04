package com.sy.side.dashboard.infrastructure;

import com.sy.side.dashboard.application.port.out.DashboardHoldingQueryPort;
import com.sy.side.dashboard.dto.DashboardHoldingRow;
import com.sy.side.dashboard.infrastructure.mybatis.DashboardHoldingQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardHoldingQueryPersistenceAdapter implements DashboardHoldingQueryPort {

    private final DashboardHoldingQueryRepository dashboardHoldingQueryRepository;

    @Override
    public List<DashboardHoldingRow> findHoldingsByMemberId(Long memberId) {
        return dashboardHoldingQueryRepository.findHoldingsByMemberId(memberId);
    }
}