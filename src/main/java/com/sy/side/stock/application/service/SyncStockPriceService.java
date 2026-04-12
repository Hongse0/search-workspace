package com.sy.side.stock.application.service;

import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import com.sy.side.stock.application.port.out.StockItemQueryPort;
import com.sy.side.stock.application.port.out.StockPriceApiPort;
import com.sy.side.stock.application.port.out.StockPriceCommandPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.domain.StockPriceDaily;
import com.sy.side.stock.dto.response.StockPriceApiResponse;
import com.sy.side.stock.dto.response.StockPriceSyncResponse;
import com.sy.side.stock.util.StockUtil;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SyncStockPriceService implements SyncStockPriceUseCase {

    private final StockItemQueryPort stockItemQueryPort;
    private final StockPriceApiPort stockPriceApiPort;
    private final StockPriceCommandPort stockPriceCommandPort;

    @Override
    public StockPriceSyncResponse syncSingle(String srtnCd, String basDt) {
        String normalizedDbCode = normalizeDbCode(srtnCd);
        String apiCode = normalizeApiCode(srtnCd);

        stockItemQueryPort.findBySrtnCd(normalizedDbCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목 코드입니다. srtnCd=" + srtnCd));

        int success = 0;
        int fail = 0;

        try {
            List<StockPriceApiResponse.Item> items =
                    stockPriceApiPort.fetchBySrtnCd(apiCode, resolveBasDt(basDt));

            if (items.isEmpty()) {
                fail++;
            } else {
                upsert(items.get(0), normalizedDbCode);
                success++;
            }
        } catch (Exception e) {
            fail++;
            throw new RuntimeException("주식 시세 동기화 실패. srtnCd=" + srtnCd, e);
        }

        return StockPriceSyncResponse.builder()
                .requestedCount(1)
                .successCount(success)
                .failCount(fail)
                .build();
    }

    @Override
    public StockPriceSyncResponse syncAll(String basDt) {
        List<StockItemMaster> stocks = stockItemQueryPort.findAllActive();

        int success = 0;
        int fail = 0;
        String targetBasDt = resolveBasDt(basDt);

        for (StockItemMaster stock : stocks) {
            try {
                String dbCode = stock.getSrtnCd();
                String apiCode = normalizeApiCode(dbCode);

                List<StockPriceApiResponse.Item> items =
                        stockPriceApiPort.fetchBySrtnCd(apiCode, targetBasDt);

                if (items.isEmpty()) {
                    fail++;
                    continue;
                }

                upsert(items.get(0), dbCode);
                success++;
            } catch (Exception e) {
                fail++;
            }
        }

        return StockPriceSyncResponse.builder()
                .requestedCount(stocks.size())
                .successCount(success)
                .failCount(fail)
                .build();
    }

    private void upsert(StockPriceApiResponse.Item item, String dbSrtnCd) {
        StockPriceDaily entity = stockPriceCommandPort.findBySrtnCdAndBasDt(dbSrtnCd, item.getBasDt())
                .orElseGet(() -> StockPriceDaily.create(
                        dbSrtnCd,
                        item.getBasDt(),
                        item.getIsinCd(),
                        item.getItmsNm(),
                        item.getMrktCtg(),
                        parseLong(item.getClpr()),
                        parseLong(item.getVs()),
                        parseDecimal(item.getFltRt()),
                        parseLong(item.getMkp()),
                        parseLong(item.getHipr()),
                        parseLong(item.getLopr()),
                        parseLong(item.getTrqu()),
                        parseLong(item.getTrPrc()),
                        parseLong(item.getLstgStCnt()),
                        parseLong(item.getMrktTotAmt())
                ));

        entity.updateFrom(
                item.getIsinCd(),
                item.getItmsNm(),
                item.getMrktCtg(),
                parseLong(item.getClpr()),
                parseLong(item.getVs()),
                parseDecimal(item.getFltRt()),
                parseLong(item.getMkp()),
                parseLong(item.getHipr()),
                parseLong(item.getLopr()),
                parseLong(item.getTrqu()),
                parseLong(item.getTrPrc()),
                parseLong(item.getLstgStCnt()),
                parseLong(item.getMrktTotAmt())
        );

        stockPriceCommandPort.save(entity);
    }

    private String normalizeDbCode(String srtnCd) {
        if (srtnCd == null || srtnCd.isBlank()) {
            throw new IllegalArgumentException("종목 코드가 비어 있습니다.");
        }
        String trimmed = srtnCd.trim();
        return trimmed.startsWith("A") ? trimmed : "A" + trimmed;
    }

    private String normalizeApiCode(String srtnCd) {
        if (srtnCd == null || srtnCd.isBlank()) {
            throw new IllegalArgumentException("종목 코드가 비어 있습니다.");
        }
        String trimmed = srtnCd.trim();
        return trimmed.startsWith("A") ? trimmed.substring(1) : trimmed;
    }

    private String resolveBasDt(String basDt) {
        if (basDt != null && !basDt.isBlank()) {
            return basDt;
        }
        return StockUtil.resolveKrxBaseDate(LocalDateTime.now());
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Long.parseLong(value.replaceAll(",", ""));
    }

    private BigDecimal parseDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new BigDecimal(value.replaceAll(",", ""));
    }
}