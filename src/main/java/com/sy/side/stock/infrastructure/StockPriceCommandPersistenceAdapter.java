package com.sy.side.stock.infrastructure;

import com.sy.side.stock.application.port.out.StockPriceCommandPort;
import com.sy.side.stock.domain.StockPriceDaily;
import com.sy.side.stock.infrastructure.jpa.StockPriceDailyRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockPriceCommandPersistenceAdapter implements StockPriceCommandPort {

    private final StockPriceDailyRepository stockPriceDailyRepository;

    @Override
    public StockPriceDaily save(StockPriceDaily stockPriceDaily) {
        return stockPriceDailyRepository.save(stockPriceDaily);
    }

    @Override
    public Optional<StockPriceDaily> findBySrtnCdAndBasDt(String srtnCd, String basDt) {
        return stockPriceDailyRepository.findBySrtnCdAndBasDt(srtnCd, basDt);
    }
}