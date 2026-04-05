package com.sy.side.stock.infrastructure;

import com.sy.side.account.domain.Account;
import com.sy.side.account.infrastructure.jpa.AccountRepository;
import com.sy.side.stock.application.dto.command.ApplyBuyCommand;
import com.sy.side.stock.application.port.out.AccountPositionCommandPort;
import com.sy.side.stock.domain.AccountPosition;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.infrastructure.jpa.AccountPositionRepository;
import com.sy.side.stock.repository.StockItemMasterRepo;
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

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}