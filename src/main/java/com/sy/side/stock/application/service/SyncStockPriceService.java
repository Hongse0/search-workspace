package com.sy.side.stock.application.service;

import com.sy.side.stock.application.port.in.SyncStockPriceUseCase;
import com.sy.side.stock.application.port.out.LoadEtfPriceInfoPort;
import com.sy.side.stock.application.port.out.StockItemQueryPort;
import com.sy.side.stock.application.port.out.StockPriceApiPort;
import com.sy.side.stock.application.port.out.StockPriceCommandPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.domain.StockPriceDaily;
import com.sy.side.stock.dto.response.EtfPriceInfoItem;
import com.sy.side.stock.dto.response.StockPriceApiResponse;
import com.sy.side.stock.dto.response.StockPriceSyncResponse;
import com.sy.side.stock.util.StockUtil;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SyncStockPriceService implements SyncStockPriceUseCase {

    private final StockItemQueryPort stockItemQueryPort;
    private final StockPriceApiPort stockPriceApiPort;
    private final LoadEtfPriceInfoPort loadEtfPriceInfoPort;
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
        List<StockItemMaster> activeItems = stockItemQueryPort.findAllActive();
        List<StockItemMaster> stocks = activeItems.stream()
                .filter(StockItemMaster::isStock)
                .toList();
        List<StockItemMaster> etfs = activeItems.stream()
                .filter(StockItemMaster::isEtf)
                .toList();

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

        PriceSyncCount etfCount = syncEtfPrices(etfs, targetBasDt);
        success += etfCount.success();
        fail += etfCount.fail();

        return StockPriceSyncResponse.builder()
                .requestedCount(stocks.size() + etfs.size())
                .successCount(success)
                .failCount(fail)
                .build();
    }

    private PriceSyncCount syncEtfPrices(List<StockItemMaster> etfs, String targetBasDt) {
        if (etfs.isEmpty()) {
            return new PriceSyncCount(0, 0);
        }

        List<EtfPriceInfoItem> items;
        try {
            items = loadEtfPriceInfoPort.loadEtfPriceInfos(targetBasDt);
        } catch (Exception e) {
            log.warn("[ETF_PRICE_SYNC] API failed. basDt={}, requested={}", targetBasDt, etfs.size(), e);
            return new PriceSyncCount(0, etfs.size());
        }

        Map<String, EtfPriceInfoItem> itemByApiCode = items.stream()
                .filter(item -> item.getSrtnCd() != null && !item.getSrtnCd().isBlank())
                .collect(Collectors.toMap(
                        item -> item.getSrtnCd().trim(),
                        Function.identity(),
                        (left, right) -> left
                ));

        int success = 0;
        int fail = 0;

        for (StockItemMaster etf : etfs) {
            try {
                String apiCode = normalizeApiCode(etf.getSrtnCd());
                EtfPriceInfoItem item = itemByApiCode.get(apiCode);

                if (item == null) {
                    fail++;
                    continue;
                }

                upsertEtf(item, etf.getSrtnCd());
                success++;
            } catch (Exception e) {
                fail++;
                log.warn("[ETF_PRICE_SYNC] item failed. basDt={}, srtnCd={}", targetBasDt, etf.getSrtnCd(), e);
            }
        }

        return new PriceSyncCount(success, fail);
    }

    private void upsertEtf(EtfPriceInfoItem item, String dbSrtnCd) {
        StockPriceDaily entity = stockPriceCommandPort.findBySrtnCdAndBasDt(dbSrtnCd, item.getBasDt())
                .orElseGet(() -> StockPriceDaily.create(
                        dbSrtnCd,
                        item.getBasDt(),
                        item.getIsinCd(),
                        item.getItmsNm(),
                        "ETF",
                        parseLong(item.getClpr()),
                        parseLong(item.getVs()),
                        parseDecimal(item.getFltRt()),
                        parseLong(item.getMkp()),
                        parseLong(item.getHipr()),
                        parseLong(item.getLopr()),
                        parseLong(item.getTrqu()),
                        parseLong(item.getTrPrc()),
                        parseLong(item.getStLstgCnt()),
                        parseLong(item.getMrktTotAmt())
                ));

        entity.updateFrom(
                item.getIsinCd(),
                item.getItmsNm(),
                "ETF",
                parseLong(item.getClpr()),
                parseLong(item.getVs()),
                parseDecimal(item.getFltRt()),
                parseLong(item.getMkp()),
                parseLong(item.getHipr()),
                parseLong(item.getLopr()),
                parseLong(item.getTrqu()),
                parseLong(item.getTrPrc()),
                parseLong(item.getStLstgCnt()),
                parseLong(item.getMrktTotAmt())
        );

        stockPriceCommandPort.save(entity);
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

    private record PriceSyncCount(int success, int fail) {
    }
}
