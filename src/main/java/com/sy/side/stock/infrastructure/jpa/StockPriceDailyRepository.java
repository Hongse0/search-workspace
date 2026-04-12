package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockPriceDaily;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceDailyRepository extends JpaRepository<StockPriceDaily, Long> {
    Optional<StockPriceDaily> findBySrtnCdAndBasDt(String srtnCd, String basDt);
    Optional<StockPriceDaily> findTopBySrtnCdOrderByBasDtDesc(String srtnCd);
}