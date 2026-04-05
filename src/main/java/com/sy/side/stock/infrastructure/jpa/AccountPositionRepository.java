package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.AccountPosition;
import com.sy.side.trade.domain.Market;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountPositionRepository extends JpaRepository<AccountPosition, Long> {

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