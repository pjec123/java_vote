-- =================================================================
-- 2. 投票系統 資料操作與範例腳本 (DML)
-- =================================================================

-- -----------------------------------------------------------------
-- [A] 初始測試資料 (模擬後台手動新增)
-- -----------------------------------------------------------------
INSERT INTO `voting_item` (`Item`) VALUES ('炸雞');
INSERT INTO `voting_item` (`Item`) VALUES ('披薩');
INSERT INTO `voting_item` (`Item`) VALUES ('漢堡');

-- -----------------------------------------------------------------
-- [B] 前台模擬投票紀錄 (模擬使用者線上多選投票)
-- -----------------------------------------------------------------
-- 假設 Lilia 同時投給了 炸雞(ID:1) 與 披薩(ID:2)
INSERT INTO `voting_record` (`Voters`, `voting_item_id`) VALUES ('Lilia', 1);
INSERT INTO `voting_record` (`Voters`, `voting_item_id`) VALUES ('Lilia', 2);

-- 假設 Tom 投給了 披薩(ID:2)
INSERT INTO `voting_record` (`Voters`, `voting_item_id`) VALUES ('Tom', 2);

-- -----------------------------------------------------------------
-- [C] 系統常用核心查詢 (對應後端 Stored Procedure 內部邏輯)
-- -----------------------------------------------------------------

-- 1. 後台：查詢所有可投票項目清單
SELECT `id`, `Item` FROM `voting_item`;

-- 2. 後台：指定項目刪除範例 (例如刪除 ID 為 3 的項目)
-- DELETE FROM `voting_item` WHERE `id` = 3;

-- 3. 前台：即時統計各項目累積票數看板 (LEFT JOIN 核心算法)
SELECT 
    vi.`id`, 
    vi.`Item`, 
    COUNT(vr.`id`) AS `vote_count`
FROM `voting_item` vi
LEFT JOIN `voting_record` vr ON vi.`id` = vr.`voting_item_id`
GROUP BY vi.`id`, vi.`Item`;