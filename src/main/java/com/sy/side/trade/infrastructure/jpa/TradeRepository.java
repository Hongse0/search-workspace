package com.sy.side.trade.infrastructure.jpa;

import com.sy.side.trade.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade,Long> {
    void deleteByAccount_AccountId(Long accountId);
}
