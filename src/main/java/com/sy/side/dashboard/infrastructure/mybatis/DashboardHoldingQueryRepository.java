package com.sy.side.dashboard.infrastructure.mybatis;

import com.sy.side.dashboard.dto.DashboardHoldingRow;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardHoldingQueryRepository {
    List<DashboardHoldingRow> findHoldingsByMemberId(Long memberId);
}
