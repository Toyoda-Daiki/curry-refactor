-- statusの有効な値を0, 1, 2に制限
ALTER TABLE orders
  ADD CONSTRAINT chk_orders_status
  CHECK (status IN (0, 1, 2));

-- payment_methodの有効な値を1, 2に制限
ALTER TABLE orders
  ADD CONSTRAINT chk_orders_payment_method
  CHECK (payment_method IN (1, 2));

-- カラムの意味をコメントで記録
COMMENT ON COLUMN orders.status IS '注文ステータス: 0=カート中, 1=クレジット決済済み, 2=代引き';
COMMENT ON COLUMN orders.payment_method IS '支払い方法: 1=クレジットカード, 2=代引き';
