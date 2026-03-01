package com.sy.side.stock.infrastructure;

import com.sy.side.account.entity.Account;
import com.sy.side.account.repository.AccountRepository;
import com.sy.side.stock.application.dto.command.ApplyBuyCommand;
import com.sy.side.stock.application.port.out.AccountPositionCommandPort;
import com.sy.side.stock.domain.AccountPosition;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.infrastructure.jpa.AccountPositionRepository;
import com.sy.side.stock.repository.StockItemMasterRepo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

        Optional<AccountPosition> optional = accountPositionRepository.findForUpdate(accountId, stockId);

        BigDecimal buyPrice = cmd.getBuyPrice();
        Long buyQty = cmd.getBuyQuantity();

        BigDecimal buyCost = buyPrice.multiply(BigDecimal.valueOf(buyQty));

        if (optional.isEmpty()) {
            Account accountRef = accountRepository.getReferenceById(accountId);
            StockItemMaster stockRef = stockItemMasterRepo.getReferenceById(stockId);

            AccountPosition created = AccountPosition.builder()
                    .account(accountRef)
                    .stock(stockRef)
                    .market(cmd.getMarket())
                    .quantity(buyQty)
                    .avgPrice(buyPrice)
                    .costAmount(buyCost.setScale(2, RoundingMode.HALF_UP))
                    .realizedPnl(BigDecimal.ZERO)
                    .lastTradeId(cmd.getTradeId())
                    .updatedAt(now())
                    .build();

            accountPositionRepository.save(created);
            return;
        }

        AccountPosition pos = optional.get();

        Long oldQty = pos.getQuantity();
        BigDecimal oldCost = pos.getCostAmount();

        Long newQty = oldQty + buyQty;
        BigDecimal newCost = oldCost.add(buyCost);

        BigDecimal newAvg = newCost
                .divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP);

        pos.applySnapshot(
                newQty,
                newAvg,
                newCost.setScale(2, RoundingMode.HALF_UP),
                pos.getRealizedPnl(),
                cmd.getTradeId()
        );

    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }
}
