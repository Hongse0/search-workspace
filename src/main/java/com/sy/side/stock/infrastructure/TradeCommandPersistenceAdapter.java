package com.sy.side.stock.infrastructure;

import com.sy.side.account.domain.Account;
import com.sy.side.trade.domain.Trade;
import com.sy.side.account.infrastructure.jpa.AccountRepository;
import com.sy.side.stock.application.dto.command.TradeInsertCommand;
import com.sy.side.trade.application.port.out.TradeCommandPort;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.trade.infrastructure.jpa.TradeRepository;
import com.sy.side.stock.repository.StockItemMasterRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeCommandPersistenceAdapter implements TradeCommandPort {

    private final TradeRepository tradeRepository;
    private final AccountRepository accountRepository;
    private final StockItemMasterRepo stockItemMasterRepo;

    @Transactional
    @Override
    public Long insertTrade(TradeInsertCommand cmd) {

        Account accountRef = accountRepository.getReferenceById(cmd.accountId());
        StockItemMaster stockRef = (cmd.stockId() == null)
                ? null
                : stockItemMasterRepo.getReferenceById(cmd.stockId());

        Trade trade = Trade.builder()
                .account(accountRef)
                .stock(stockRef)
                .market(cmd.market())
                .side(cmd.side())
                .quantity(cmd.quantity())
                .price(cmd.price())
                .fee(cmd.fee())
                .tax(cmd.tax())
                .totalAmount(cmd.totalAmount())
                .tradeDateTime(cmd.tradeDateTime())
                .memo(cmd.memo())
                .build();

        return tradeRepository.save(trade).getTradeId();
    }

    @Override
    public void deleteByAccountId(Long accountId) {

    }
}

