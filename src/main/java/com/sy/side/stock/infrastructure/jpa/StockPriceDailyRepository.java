package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockPriceDaily;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    List<StockPriceRow> findLatestPrices(@Param("stockIds") Set<Long> stockIds);

    interface StockPriceRow {
        Long getStockId();
        Long getClosePrice();
    }

    @Query(value = """
    SELECT
        spd.srtn_cd AS srtnCd,
        spd.bas_dt AS basDt,
        spd.clpr AS closePrice,
        spd.vs AS vs,
        spd.flt_rt AS fltRt
    FROM stock_price_daily spd
    WHERE spd.srtn_cd IN (:srtnCds)
    AND spd.bas_dt = (
        SELECT MAX(spd2.bas_dt)
        FROM stock_price_daily spd2
        WHERE spd2.srtn_cd = spd.srtn_cd
    )
""", nativeQuery = true)
    List<StockPriceByCodeRow> findLatestPricesBySrtnCd(@Param("srtnCds") Set<String> srtnCds);

    interface StockPriceByCodeRow {
        String getSrtnCd();
        String getBasDt();
        Long getClosePrice();
        Long getVs();
        BigDecimal getFltRt();
    }

    @Modifying
    @Query("DELETE FROM StockPriceDaily s WHERE s.basDt < :baseDate")
    int deleteByBasDtBefore(@Param("baseDate") String baseDate);
}
