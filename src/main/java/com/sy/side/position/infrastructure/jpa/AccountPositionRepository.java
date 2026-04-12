package com.sy.side.position.infrastructure.jpa;

import com.sy.side.position.domain.AccountPosition;
import com.sy.side.trade.domain.Market;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountPositionRepository extends JpaRepository<AccountPosition, Long> {

    @Query("""
        select coalesce(sum(ap.costAmount), 0)
        from AccountPosition ap
        where ap.account.accountId = :accountId
          and ap.quantity > 0
    """)
    BigDecimal sumStockAssetValueByAccountId(@Param("accountId") Long accountId);

    @Query("""
        select count(ap)
        from AccountPosition ap
        where ap.account.accountId = :accountId
          and ap.quantity > 0
    """)
    Long countHoldingByAccountId(@Param("accountId") Long accountId);

    @Query("""
        select ap
        from AccountPosition ap
        where ap.account.accountId = :accountId
          and ap.stock.id = :stockId
    """)
    Optional<AccountPosition> findByAccountIdAndStockId(
            @Param("accountId") Long accountId,
            @Param("stockId") Long stockId
    );

    @Query("""
        select ap
        from AccountPosition ap
        where ap.account.accountId = :accountId
          and ap.quantity > 0
    """)
    List<AccountPosition> findAllByAccountId(@Param("accountId") Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select p
        from AccountPosition p
        where p.account.accountId = :accountId
          and p.stock.id = :stockId
          and p.market = :market
    """)
    Optional<AccountPosition> findForUpdate(
            @Param("accountId") Long accountId,
            @Param("stockId") Long stockId,
            @Param("market") Market market
    );

    void deleteByAccount_AccountId(Long accountId);
}