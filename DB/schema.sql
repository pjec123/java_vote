-- =================================================================
-- 1. 投票系統 資料表與全功能預存程序建置腳本 (DDL)
-- =================================================================

-- -----------------------------------------------------------------
-- [A] 刪除舊有物件（注意順序：紀錄表有外鍵，需先刪除）
-- -----------------------------------------------------------------
DROP PROCEDURE IF EXISTS `sp_get_voting_summary`;
DROP PROCEDURE IF EXISTS `sp_get_all_items`;
DROP PROCEDURE IF EXISTS `sp_insert_item`;
DROP PROCEDURE IF EXISTS `sp_delete_item`;
DROP PROCEDURE IF EXISTS `sp_insert_voting_record`;

DROP TABLE IF EXISTS `voting_record`;
DROP TABLE IF EXISTS `voting_item`;

-- -----------------------------------------------------------------
-- [B] 建立資料表結構
-- -----------------------------------------------------------------

-- 1. 建立投票項目表 (voting_item)
CREATE TABLE `voting_item` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '流水號，主鍵',
  `Item` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '投票項目名稱',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='投票項目主表';

-- 2. 建立投票紀錄表 (voting_record)
CREATE TABLE `voting_record` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '流水號',
  `Voters` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '' COMMENT '投票人姓名',
  `voting_item_id` INT DEFAULT 0 COMMENT '投票項目id',
  `time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '投票時間',
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_voting_item_id` FOREIGN KEY (`voting_item_id`) REFERENCES `voting_item` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='使用者投票紀錄明細表';


-- -----------------------------------------------------------------
-- [C] 建立全功能預存程序 (Stored Procedures)
-- -----------------------------------------------------------------
DELIMITER //

-- 1. 獲取前台累積票數統計摘要
CREATE PROCEDURE `sp_get_voting_summary`()
BEGIN
    SELECT 
        vi.`id`, 
        vi.`Item`, 
        COUNT(vr.`id`) AS `vote_count`
    FROM `voting_item` vi
    LEFT JOIN `voting_record` vr ON vi.`id` = vr.`voting_item_id`
    GROUP BY vi.`id`, vi.`Item`;
END //

-- 2. 後台：查詢所有投票項目清單
CREATE PROCEDURE `sp_get_all_items`()
BEGIN
    SELECT `id`, `Item` FROM `voting_item` ORDER BY `id` ASC;
END //

-- 3. 後台：新增投票項目 (帶有輸入參數 p_item_name)
CREATE PROCEDURE `sp_insert_item`(
    IN p_item_name VARCHAR(255)
)
BEGIN
    INSERT INTO `voting_item` (`Item`) VALUES (p_item_name);
END //

-- 4. 後台：刪除指定投票項目 (帶有輸入參數 p_item_id)
CREATE PROCEDURE `sp_delete_item`(
    IN p_item_id INT
)
BEGIN
    DELETE FROM `voting_item` WHERE `id` = p_item_id;
END //

-- 5. 前台：寫入單筆投票紀錄 (帶有輸入參數，供後端 Service 跑迴圈多選寫入)
CREATE PROCEDURE `sp_insert_voting_record`(
    IN p_voter_name VARCHAR(255),
    IN p_item_id INT
)
BEGIN
    INSERT INTO `voting_record` (`Voters`, `voting_item_id`) VALUES (p_voter_name, p_item_id);
END //

DELIMITER ;
