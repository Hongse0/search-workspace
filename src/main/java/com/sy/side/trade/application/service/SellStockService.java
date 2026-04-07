package com.sy.side.trade.application.service;

import static com.sy.side.stock.util.StockUtil.defaultZero;

import com.sy.side.account.application.port.out.AccountCommandPort;
import com.sy.side.account.application.port.out.AccountQueryPort;
import com.sy.side.account.domain.Account;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import com.sy.side.position.application.port.out.AccountPositionQueryPort;
import com.sy.side.stock.application.dto.command.TradeInsertCommand;
import com.sy.side.stock.application.port.out.AccountPositionCommandPort;
import com.sy.side.stock.application.port.out.StockItemMasterQueryPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.trade.application.port.in.SellStockUseCase;
import com.sy.side.trade.application.port.out.TradeCommandPort;
import com.sy.side.trade.domain.Market;
import com.sy.side.trade.domain.TradeSide;
import com.sy.side.trade.dto.AccountPositionSummary;
import com.sy.side.trade.dto.ApplySellCommand;
import com.sy.side.trade.dto.request.SellStockRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellStockService implements SellStockUseCase {

    private final TradeCommandPort tradeCommandPort;
    private final AccountPositionCommandPort accountPositionCommandPort;
    private final AccountPositionQueryPort accountPositionQueryPort;
    private final AccountQueryPort accountQueryPort;
    private final AccountCommandPort accountCommandPort;
    private final StockItemMasterQueryPort stockItemMasterQueryPort;

    @Transactional
    @Override
    public void sellKorea(SellStockRequest req) {
        validateRequest(req);

        Account account = accountQueryPort.findById(req.getAccountId())
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));
        account.validateActive();

        StockItemMaster stock = stockItemMasterQueryPort.findBySrtnCd(req.getSymbolCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 종목 코드입니다."));

        BigDecimal fee = defaultZero(req.getFee());
        BigDecimal tax = defaultZero(req.getTax());

        LocalDateTime tradeDateTime = req.getTradeDateTime() != null
                ? req.getTradeDateTime()
                : LocalDateTime.now();

        Long stockId = stock.getId();

        AccountPositionSummary position = accountPositionQueryPort
                .findByAccountIdAndStockId(req.getAccountId(), stockId)
                .orElseThrow(() -> new IllegalArgumentException("보유 중인 종목이 아닙니다."));

        if (position.getQuantity() < req.getQuantity()) {
            throw new IllegalArgumentException("보유 수량보다 많이 매도할 수 없습니다.");
        }

        BigDecimal grossAmount = req.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        BigDecimal sellAmount = grossAmount.subtract(fee).subtract(tax);

        if (sellAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("정산 금액은 0보다 커야 합니다.");
        }

        BigDecimal totalAmount = sellAmount;

        TradeInsertCommand command = new TradeInsertCommand(
                req.getAccountId(),
                stockId,
                Market.KR,
                TradeSide.SELL,
                Long.valueOf(req.getQuantity()),
                req.getPrice(),
                fee,
                tax,
                totalAmount,
                tradeDateTime,
                req.getMemo()
        );

        Long tradeId = tradeCommandPort.insertTrade(command);

        accountPositionCommandPort.applySell(new ApplySellCommand(
                req.getAccountId(),
                stockId,
                Market.KR,
                req.getQuantity(),
                req.getPrice(),
                fee,
                tax,
                tradeId,
                tradeDateTime
        ));

        accountCommandPort.depositCash(req.getAccountId(), sellAmount);
    }

    private void validateRequest(SellStockRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("요청값이 없습니다.");
        }
        if (req.getAccountId() == null) {
            throw new IllegalArgumentException("계좌 ID는 필수입니다.");
        }
        if (req.getSymbolCode() == null || req.getSymbolCode().isBlank()) {
            throw new IllegalArgumentException("주식 코드는 필수입니다.");
        }
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("매도 수량은 1 이상이어야 합니다.");
        }
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("매도 단가는 0보다 커야 합니다.");
        }
        if (req.getFee() != null && req.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("수수료는 0 이상이어야 합니다.");
        }
        if (req.getTax() != null && req.getTax().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("세금은 0 이상이어야 합니다.");
        }
    }
}