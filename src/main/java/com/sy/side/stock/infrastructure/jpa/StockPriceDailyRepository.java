package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockPriceDaily;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StockPriceDailyRepository extends JpaRepository<StockPriceDaily, Long> {
    Optional<StockPriceDaily> findBySrtnCdAndBasDt(String srtnCd, String basDt);
    Optional<StockPriceDaily> findTopBySrtnCdOrderByBasDtDesc(String srtnCd);
    @Query(value = """
    SELECT 
        sim.id AS stockId,
        spd.clpr AS closePrice
    FROM stock_item_master sim
    JOIN stock_price_daily spd 
        ON sim.srtn_cd = spd.srtn_cd
    WHERE sim.id IN (:stockIds)
    AND spd.bas_dt = (
        SELECT MAX(spd2.bas_dt)
        FROM stock_price_daily spd2
        WHERE spd2.srtn_cd = spd.srtn_cd
    )
""", nativeQuery = true)
    List<StockPriceRow> findLatestPrices(Set<Long> stockIds);

    interface StockPriceRow {
        Long getStockId();
        Long getClosePrice();
    }
}