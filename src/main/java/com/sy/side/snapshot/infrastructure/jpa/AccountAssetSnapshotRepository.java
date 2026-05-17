package com.sy.side.snapshot.infrastructure.jpa;

import com.sy.side.snapshot.domain.AccountAssetSnapshot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountAssetSnapshotRepository extends JpaRepository<AccountAssetSnapshot, Long> {

    Optional<AccountAssetSnapshot> findByAccountIdAndSnapshotYm(Long accountId, String snapshotYm);

    List<AccountAssetSnapshot> findAllByMemberIdAndSnapshotYmGreaterThanEqualOrderBySnapshotYmAsc(
            Long memberId,
            String snapshotYm
    );

    @Query("""
        select s.snapshotYm as snapshotYm,
               sum(s.cashBalance) as cashBalance,
               sum(s.stockEvaluationAmount) as stockEvaluationAmount,
               sum(s.totalAssetValue) as totalAssetValue,
               sum(s.holdingCount) as holdingCount
        from AccountAssetSnapshot s
        where s.memberId = :memberId
          and s.snapshotYm >= :fromYm
        group by s.snapshotYm
        order by s.snapshotYm asc
    """)
    List<MemberMonthlyAssetRow> findMonthlyAggregateByMemberId(
            @Param("memberId") Long memberId,
            @Param("fromYm") String fromYm
    );

    interface MemberMonthlyAssetRow {
        String getSnapshotYm();
        java.math.BigDecimal getCashBalance();
        java.math.BigDecimal getStockEvaluationAmount();
        java.math.BigDecimal getTotalAssetValue();
        Long getHoldingCount();
    }
}
