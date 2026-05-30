CREATE TABLE account_weekly_asset_snapshot (
    weekly_snapshot_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    snapshot_date DATE NOT NULL,
    cash_balance DECIMAL(18, 2) NOT NULL,
    stock_evaluation_amount DECIMAL(18, 2) NOT NULL,
    total_asset_value DECIMAL(18, 2) NOT NULL,
    holding_count BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (weekly_snapshot_id),
    CONSTRAINT uk_weekly_snapshot_account_date UNIQUE (account_id, snapshot_date),
    INDEX idx_weekly_snapshot_member_date (member_id, snapshot_date),
    INDEX idx_weekly_snapshot_date (snapshot_date)
);
