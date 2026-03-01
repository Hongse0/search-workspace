package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.account.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade,Long> {
}
