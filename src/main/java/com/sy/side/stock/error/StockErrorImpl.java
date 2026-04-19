package com.sy.side.stock.error;

import com.sy.side.common.error.ErrorCode;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;

public enum StockErrorImpl implements ErrorCode {

    STOCK_ERROR(
            "30001",
            HttpStatus.INTERNAL_SERVER_ERROR,
            Collections.singletonList("종목 처리 중 오류가 발생했습니다.")
    ),
    STOCK_NOT_FOUND(
            "30002",
            HttpStatus.NOT_FOUND,
            Collections.singletonList("존재하지 않는 종목입니다.")
    ),
    INVALID_STOCK_CODE(
            "30003",
            HttpStatus.BAD_REQUEST,
            Collections.singletonList("유효하지 않은 종목 코드입니다.")
    ),
    STOCK_SEARCH_KEYWORD_REQUIRED(
            "30004",
            HttpStatus.BAD_REQUEST,
            Collections.singletonList("검색어를 입력해주세요.")
    ),
    STOCK_SEARCH_SIZE_INVALID(
            "30005",
            HttpStatus.BAD_REQUEST,
            Collections.singletonList("조회 건수는 1 이상 50 이하여야 합니다.")
    );

    private final String code;
    private final HttpStatus status;
    private final List<String> messages;

    StockErrorImpl(final String code, final HttpStatus status, final List<String> messages) {
        this.code = code;
        this.status = status;
        this.messages = messages;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public List<String> getMessages() {
        return this.messages;
    }
}