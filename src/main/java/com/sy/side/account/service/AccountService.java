package com.sy.side.account.service;

import static com.sy.side.account.util.AccoutUtil.nvl;
import static com.sy.side.account.util.AccoutUtil.validateBuyRequest;

import com.sy.side.account.dto.request.AccountCreateRequest;
import com.sy.side.stock.dto.request.BuyStockRequest;
import com.sy.side.account.dto.response.AccountResponse;
import com.sy.side.account.dto.response.AccountSelectResponse;
import com.sy.side.account.entity.Account;
import com.sy.side.account.entity.Trade;
import com.sy.side.account.entity.TradeSide;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.account.repository.AccountRepository;
import com.sy.side.account.repository.TradeRepo;
import com.sy.side.common.exception.BizException;
import com.sy.side.stock.domain.StockItemMaster;
import com.sy.side.stock.repository.StockItemMasterRepo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final StockItemMasterRepo stockItemMasterRepo;
    private final TradeRepo tradeRepo;

    public AccountResponse createAccount(Long memberId, AccountCreateRequest request) {

        if (accountRepository.existsByMemberIdAndBrokerNameAndAccountNumber(
                memberId,
                request.getBrokerName(),
                request.getAccountNumber()
        )) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
        BigDecimal init = request.getInitialBalance();
        if (init == null) init = BigDecimal.ZERO;

        init = init.setScale(2, RoundingMode.DOWN);

        Account account = Account.builder()
                .memberId(memberId)
                .brokerName(request.getBrokerName())
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .baseCurrency(request.getBaseCurrency())
                .cashBalance(init)
                .build();

        try {
            Account saved = accountRepository.save(account);
            return new AccountResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
    }

    /**
     * 내 계좌 전체 조회
     */
    @Transactional(readOnly = true)
    public List<AccountSelectResponse> findAllAccount(Long memberId) {
        return accountRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(AccountSelectResponse::new)
                .toList();
    }

    /**
     * 내 계좌 단건 조회
     */
    @Transactional(readOnly = true)
    public AccountSelectResponse findMyAccount(Long memberId, Long accountId) {
        Account account = accountRepository.findByAccountIdAndMemberId(accountId, memberId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        return new AccountSelectResponse(account);
    }

    @Transactional
    public void deleteAccount(Long memberId, Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        if (!account.getMemberId().equals(memberId)) {
            throw new BizException(AccountErrorImpl.ACCOUNT_FORBIDDEN);
        }

        accountRepository.delete(account);
    }

    @Transactional
    public Long insertStockInfo(BuyStockRequest req) {
        // 1) 계좌 조회
        Account account = accountRepository.findById(req.getAccountId())
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_NOT_FOUND));

        // 2) 종목 조회
        StockItemMaster stock = stockItemMasterRepo.findById(req.getStockId())
                .orElseThrow(() -> new BizException(AccountErrorImpl.ACCOUNT_ERROR)); // 여긴 StockError로 분리 추천

        // 3) 입력값 검증 (필수)
        validateBuyRequest(req);

        // 4) 금액 계산 (BigDecimal 권장)
        BigDecimal price = req.getPrice();                 // 체결 단가
        BigDecimal qty = BigDecimal.valueOf(req.getQuantity());

        BigDecimal fee = nvl(req.getFee());                // null이면 0
        BigDecimal tax = nvl(req.getTax());                // null이면 0

        // 매수 총액 = 단가*수량 + 수수료 + 세금
        BigDecimal gross = price.multiply(qty);            // 체결금액
        BigDecimal totalAmount = gross.add(fee).add(tax);

        // 5) 잔고 확인/차감 (Account에 cashBalance 같은 필드가 있다고 가정)
        // 만약 계좌 잔고 필드명이 다르면 여기만 바꾸면 됨.
        if (account.getCashBalance().compareTo(totalAmount) < 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
        account.decreaseCash(totalAmount); // account 내부에 메서드로 처리 추천 (아래 참고)

        // 6) Trade 엔티티 생성 & 저장
        Trade trade = Trade.builder()
                .account(account)
                .stock(stock)
                .side(TradeSide.BUY)
                .quantity(req.getQuantity())
                .price(price)
                .fee(fee)
                .tax(tax)
                .totalAmount(totalAmount)
                .tradeDateTime(
                        req.getTradeDateTime() != null ? req.getTradeDateTime() : LocalDateTime.now()
                )
                .memo(req.getMemo())
                .build();

        Trade saved = tradeRepo.save(trade);

        // 7) (옵션) 보유수량/평단 업데이트 로직 호출 (Holding 테이블이 있다면)
        // holdingService.applyBuy(account.getId(), stock.getId(), req.getQuantity(), price, fee, tax);

        return saved.getTradeId();
    }

}

