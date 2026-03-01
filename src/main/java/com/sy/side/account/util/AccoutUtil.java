package com.sy.side.account.util;

import com.sy.side.stock.dto.request.BuyStockRequest;
import com.sy.side.account.error.AccountErrorImpl;
import com.sy.side.common.exception.BizException;
import java.math.BigDecimal;

public final class AccoutUtil {

    public static void validateBuyRequest(BuyStockRequest req) {
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
        if (req.getFee() != null && req.getFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
        if (req.getTax() != null && req.getTax().compareTo(BigDecimal.ZERO) < 0) {
            throw new BizException(AccountErrorImpl.ACCOUNT_ERROR);
        }
    }

    public static BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

}
