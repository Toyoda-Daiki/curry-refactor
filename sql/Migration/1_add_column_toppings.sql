ALTER TABLE toppings 
ADD COLUMN stock_amount INTEGER NOT NULL DEFAULT 0;

-- 補足：外部システム（Pleasanter）からの補充数加算および、注文時の減算に使用
