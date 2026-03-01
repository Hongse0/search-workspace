package com.sy.side.stock.application.service;

import static com.sy.side.stock.util.StockUtil.defaultZero;

import com.sy.side.account.entity.Market;
import com.sy.side.account.entity.TradeSide;
import com.sy.side.stock.application.dto.command.ApplyBuyCommand;
import com.sy.side.stock.application.dto.command.TradeInsertCommand;
import com.sy.side.stock.application.port.in.BuyKoreaStockUseCase;
import com.sy.side.stock.application.port.out.AccountPositionCommandPort;
import com.sy.side.stock.application.port.out.StockItemMasterQueryPort;
import com.sy.side.stock.application.port.out.TradeCommandPort;
import com.sy.side.stock.dto.request.BuyStockRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService implements BuyKoreaStockUseCase {

    private final TradeCommandPort tradeCommandPort;
    private final AccountPositionCommandPort accountPositionCommandPort;

    @Override
    public void buyKorea(BuyStockRequest req) {

        BigDecimal fee = defaultZero(req.getFee());
        BigDecimal tax = defaultZero(req.getTax());
        LocalDateTime tradeDateTime = req.getTradeDateTime() != null
                ? req.getTradeDateTime()
                : LocalDateTime.now();

        Market market = Market.KR;
        BigDecimal grossAmount = req.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        BigDecimal totalAmount = grossAmount.add(fee).add(tax).negate();
        LocalDateTime tradeAt = req.getTradeDateTime() != null ? req.getTradeDateTime() : LocalDateTime.now();

        // Todo : assembler로 수정
        TradeInsertCommand command = new TradeInsertCommand(
                req.getAccountId(),
                req.getStockId(),
                market,
                TradeSide.BUY,
                req.getQuantity(),
                req.getPrice(),
                fee,
                tax,
                totalAmount,
                tradeDateTime,
                req.getMemo()
        );

        Long tradeId = tradeCommandPort.insertTrade(command);

        accountPositionCommandPort.applyBuy(new ApplyBuyCommand(
                req.getAccountId(),
                req.getStockId(),
                Market.KR,
                req.getQuantity(),
                req.getPrice(),
                fee,
                tax,
                tradeId,
                tradeAt
        ));
    }


}

