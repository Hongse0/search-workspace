ALTER TABLE account
    ADD COLUMN stock_asset_value DECIMAL(18, 2) NOT NULL DEFAULT 0.00 AFTER cash_balance,
    ADD COLUMN total_asset_value DECIMAL(18, 2) NOT NULL DEFAULT 0.00 AFTER stock_asset_value,
    ADD COLUMN holding_count BIGINT NOT NULL DEFAULT 0 AFTER total_asset_value;

UPDATE account
SET total_asset_value = cash_balance + stock_asset_value
WHERE total_asset_value = 0.00;
