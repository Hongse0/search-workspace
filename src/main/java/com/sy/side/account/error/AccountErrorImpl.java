package com.sy.side.account.error;

import com.sy.side.common.error.ErrorCode;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;

public enum AccountErrorImpl implements ErrorCode {

    ACCOUNT_ERROR(
            "20001",
            HttpStatus.INTERNAL_SERVER_ERROR,
            Collections.singletonList("계좌 처리 중 오류가 발생했습니다.")
    ),
    SAME_ACCOUNT_EXIST(
            "20002",
            HttpStatus.CONFLICT,
            Collections.singletonList("중복된 계좌가 이미 존재합니다.")
    ),
    ACCOUNT_NOT_FOUND(
            "20003",
            HttpStatus.NOT_FOUND,
            Collections.singletonList("계좌가 존재하지 않습니다.")
    ),
    ACCOUNT_FORBIDDEN(
            "20004",
            HttpStatus.FORBIDDEN,
            Collections.singletonList("해당 계좌에 대한 권한이 없습니다.")
    ),
    ACCOUNT_HAS_TRADES(
            "20005",
            HttpStatus.BAD_REQUEST,
            Collections.singletonList("거래내역이 존재하는 계좌는 삭제할 수 없습니다.")
    ),
    ACCOUNT_INACTIVE(
            "20006",
            HttpStatus.BAD_REQUEST,
            Collections.singletonList("비활성화된 계좌입니다.")
    ),
    INSUFFICIENT_BALANCE(
            "20007",
            HttpStatus.BAD_REQUEST,
            Collections.singletonList("계좌 잔액이 부족합니다.")
    );

    private final String code;
    private final HttpStatus status;
    private final List<String> messages;

    AccountErrorImpl(final String code, final HttpStatus status, final List<String> messages) {
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