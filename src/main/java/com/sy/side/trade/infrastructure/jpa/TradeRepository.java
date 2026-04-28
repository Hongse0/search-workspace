package com.sy.side.trade.infrastructure.jpa;

import com.sy.side.trade.domain.Trade;
import com.sy.side.trade.dto.RecentTradeSummary;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    void deleteByAccount_AccountId(Long accountId);

    @Query("""
        select new com.sy.side.trade.dto.RecentTradeSummary(
            t.tradeId,
            s.id,
            s.itmsNm,
            s.srtnCd,
            cast(t.side as string),
            t.quantity,
            t.price,
            t.tradeDateTime
        )
        from Trade t
        join t.stock s
        where t.account.accountId = :accountId
        order by t.tradeDateTime desc
    """)
    List<RecentTradeSummary> findRecentTradeSummaries(
            @Param("accountId") Long accountId,
            Pageable pageable
    );

    default List<RecentTradeSummary> findRecentTradeSummaries(Long accountId, int limit) {
        return null;
    }

    @Query("""
    select new com.sy.side.trade.dto.RecentTradeSummary(
        t.tradeId,
        s.id,
        coalesce(s.itmsNm, t.symbol),
        coalesce(s.srtnCd, t.symbol),
        cast(t.side as string),
        t.quantity,
        t.price,
        t.tradeDateTime
    )
    from Trade t
    left join t.stock s
    where t.account.accountId in :accountIds
    order by t.tradeDateTime desc
""")
    List<RecentTradeSummary> findRecentTradeSummariesByAccountIds(
            @Param("accountIds") List<Long> accountIds,
            Pageable pageable
    );
}