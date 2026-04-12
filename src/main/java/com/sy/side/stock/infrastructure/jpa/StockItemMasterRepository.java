package com.sy.side.stock.infrastructure.jpa;

import com.sy.side.stock.domain.StockItemMaster;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockItemMasterRepository extends JpaRepository<StockItemMaster, Long> {
    List<StockItemMaster> findAllByActiveYn(String activeYn);
    Optional<StockItemMaster> findBySrtnCd(String srtnCd);
}