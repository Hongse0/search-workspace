package com.sy.side.snapshot.infrastructure.jpa;

import com.sy.side.snapshot.domain.AccountWeeklyAssetSnapshot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountWeeklyAssetSnapshotRepository extends JpaRepository<AccountWeeklyAssetSnapshot, Long> {

    Optional<AccountWeeklyAssetSnapshot> findByAccountIdAndSnapshotDate(Long accountId, LocalDate snapshotDate);

    @Query("""
        select s.snapshotDate as snapshotDate,
               sum(s.cashBalance) as cashBalance,
               sum(s.stockEvaluationAmount) as stockEvaluationAmount,
               sum(s.totalAssetValue) as totalAssetValue,
               sum(s.holdingCount) as holdingCount
        from AccountWeeklyAssetSnapshot s
        where s.memberId = :memberId
          and s.snapshotDate >= :fromDate
        group by s.snapshotDate
        order by s.snapshotDate asc
    """)
    List<MemberWeeklyAssetRow> findWeeklyAggregateByMemberId(
            @Param("memberId") Long memberId,
            @Param("fromDate") LocalDate fromDate
    );

    interface MemberWeeklyAssetRow {
        LocalDate getSnapshotDate();
        java.math.BigDecimal getCashBalance();
        java.math.BigDecimal getStockEvaluationAmount();
        java.math.BigDecimal getTotalAssetValue();
        Long getHoldingCount();
    }
}
