package com.sy.side.stock.infrastructure;

import com.sy.side.account.domain.Account;
import com.sy.side.account.infrastructure.jpa.AccountRepository;
import com.sy.side.position.infrastructure.jpa.AccountPositionRepository;
import com.sy.side.stock.application.dto.command.ApplyBuyCommand;
import com.sy.side.stock.application.port.out.AccountPositionCommandPort;
import com.sy.side.position.domain.AccountPosition;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.repository.StockItemMasterRepo;
import com.sy.side.trade.dto.ApplySellCommand;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AccountPositionCommandPersistenceAdapter implements AccountPositionCommandPort {

    private final AccountPositionRepository accountPositionRepository;
    private final AccountRepository accountRepository;
    private final StockItemMasterRepo stockItemMasterRepo;

    @Transactional
    @Override
    public void applyBuy(ApplyBuyCommand cmd) {
        Long accountId = cmd.getAccountId();
        Long stockId = cmd.getStockId();

        Optional<AccountPosition> optional = accountPositionRepository.findForUpdate(
                accountId,
                stockId,
                cmd.getMarket()
        );

        BigDecimal fee = nvl(cmd.getFee());
        BigDecimal tax = nvl(cmd.getTax());

        BigDecimal buyCostAmount = cmd.getPrice()
                .multiply(BigDecimal.valueOf(cmd.getQuantity()))
                .add(fee)
                .add(tax);

        if (optional.isEmpty()) {
            Account accountRef = accountRepository.getReferenceById(accountId);
            StockItemMaster stockRef = stockItemMasterRepo.getReferenceById(stockId);

            AccountPosition created = AccountPosition.open(
                    accountRef,
                    stockRef,
                    cmd.getMarket(),
                    cmd.getQuantity(),
                    buyCostAmount,
                    cmd.getTradeId()
            );

            accountPositionRepository.save(created);
            return;
        }

        AccountPosition position = optional.get();
        position.applyBuy(
                cmd.getQuantity(),
                buyCostAmount,
                cmd.getTradeId()
        );
    }

    @Transactional
    @Override
    public void deleteAllByAccountId(Long accountId) {
        accountPositionRepository.deleteByAccount_AccountId(accountId);
    }

    @Transactional
    @Override
    public void applySell(ApplySellCommand cmd) {
        Long accountId = cmd.getAccountId();
        Long stockId = cmd.getStockId();

        AccountPosition position = accountPositionRepository.findForUpdate(
                        accountId,
                        stockId,
                        cmd.getMarket()
                )
                .orElseThrow(() -> new IllegalArgumentException("보유 중인 종목이 아닙니다."));

        if (position.getQuantity() < cmd.getQuantity()) {
            throw new IllegalArgumentException("보유 수량보다 많이 매도할 수 없습니다.");
        }

        position.applySell(
                Long.valueOf(cmd.getQuantity()),
                cmd.getTradeId()
        );

        if (position.getQuantity() <= 0) {
            accountPositionRepository.delete(position);
        }
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}