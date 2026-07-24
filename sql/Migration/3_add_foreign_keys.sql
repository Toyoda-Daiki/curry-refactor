-- リファクタリング課題#15 外部キー制約の追加
ALTER TABLE orders
  ADD CONSTRAINT fk_orders_user
  FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE order_items
  ADD CONSTRAINT fk_order_items_order
  FOREIGN KEY (order_id) REFERENCES orders(id)
  ON DELETE CASCADE;

ALTER TABLE order_items
  ADD CONSTRAINT fk_order_items_item
  FOREIGN KEY (item_id) REFERENCES items(id);

ALTER TABLE order_toppings
  ADD CONSTRAINT fk_order_toppings_order_item
  FOREIGN KEY (order_item_id) REFERENCES order_items(id)
  ON DELETE CASCADE;

ALTER TABLE order_toppings
  ADD CONSTRAINT fk_order_toppings_topping
  FOREIGN KEY (topping_id) REFERENCES toppings(id);

-- 重複していた既存の外部キー制約を削除
ALTER TABLE order_items
  DROP CONSTRAINT order_items_order_id_fkey;

ALTER TABLE order_toppings
  DROP CONSTRAINT order_toppings_order_item_id_fkey;
