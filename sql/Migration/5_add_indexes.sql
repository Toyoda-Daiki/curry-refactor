-- リファクタリング課題#14 DBのパフォーマンスチェック（インデックス追加）
-- 外部キーとしてJOIN・WHERE条件に使われるカラムには、
-- フルテーブルスキャンを防ぐためインデックスが必要

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_toppings_order_item_id ON order_toppings(order_item_id);
