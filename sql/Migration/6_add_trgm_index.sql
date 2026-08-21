-- リファクタリング課題#32 LIKE検索のインデックス問題
-- LIKE '%word%' の中間一致検索はB-treeインデックスが機能しないため、
-- pg_trgm拡張機能によるトライグラムインデックスを追加する

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_items_name_trgm ON items USING GIN (name gin_trgm_ops);
