package com.sy.side.account.error;


import com.sy.side.common.error.ErrorCode;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;

public enum AccountErrorImpl implements ErrorCode {

    ACCOUNT_ERROR("20001", HttpStatus.NOT_FOUND, Collections.singletonList("계정 등록중 오류 발생.")),
    SAME_ACCOUNT_EXIST("20002",HttpStatus.NOT_FOUND, Collections.singletonList("중복된 계좌 존재")),
    ACCOUNT_NOT_FOUND("20003", HttpStatus.NOT_FOUND, Collections.singletonList("계좌 미존재")),
    ACCOUNT_FORBIDDEN("20004", HttpStatus.FORBIDDEN, Collections.singletonList("권한 없음"));

    private final String code;
    private final HttpStatus status;
    private final List<String> messages;

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

    AccountErrorImpl(final String code, final HttpStatus status, final List<String> messages) {
        this.code = code;
        this.status = status;
        this.messages = messages;
    }

}


