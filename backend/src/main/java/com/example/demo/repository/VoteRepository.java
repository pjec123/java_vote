// 資料層，負責處理和sql的溝通

package com.example.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VoteRepository {
    private final JdbcTemplate jdbcTemplate;

    public VoteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查詢所有可投票項目
    public List<Map<String, Object>> getAllItems(){
        // 呼叫資料庫裏面查詢全部項目的預設程序
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_get_all_voting_items");
        // 執行查詢並取得查詢結果
        Map<String, Object> result = jdbcCall.execute();
        // 需指定回傳的資料格式(List<Map<String, Object>>)
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    // 新增項目
    public void addItem(String name){
        // 呼叫資料庫裏面新增項目的預設程序
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_add_voting_item");
        Map<String, String> input = new HashMap<>();
        // 名稱(p_item_name)對應資料庫內容
        input.put("p_item_name", name);
        jdbcCall.execute(input);
    }

    // 刪除項目
    public void deleteItem(int index){
        // 呼叫資料庫裏面刪除項目的預設程序
        SimpleJdbcCall jdbcCell = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_delete_voting_item");
        Map<String, Object> input = new HashMap<>();
        // 名稱(p_id)對應資料庫內容
        input.put("p_id", index);
        jdbcCell.execute(input);
    }

    // 查詢所有項目票數
    public List<Map<String, Object>> selectItemSummary(){
        // 呼叫資料庫裏面查詢全部項目票數的預設程序
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_get_voting_summary");
        // 執行查詢並取得查詢結果
        Map<String, Object> result = jdbcCall.execute();
        // 需指定回傳的資料格式(List<Map<String, Object>>)
        return (List<Map<String, Object>>) result.get("#result-set-1");
    }

    // 新增投票紀錄
    public void submit(String voter, Integer id){
        // 呼叫資料庫裏面新增投票紀錄的預設程序
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("sp_insert_voting_record");
        Map<String, Object> input = new HashMap<>();
        // 名稱(p_voters和p_voting_item_id)對應資料庫內容
        input.put("p_voters", voter);
        input.put("p_voting_item_id", id);

        jdbcCall.execute(input);
    }
}