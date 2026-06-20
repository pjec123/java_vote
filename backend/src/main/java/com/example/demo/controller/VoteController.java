// 展示層，負責處理和前端的收發訊息

package com.example.demo.controller;
import com.example.demo.service.VoteService;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*") // 防止跨來源資源共用 (CORS) 錯誤
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // 查詢所有項目
    @GetMapping("/allitems")
    public ResponseEntity<List<Map<String, Object>>> getItems(){
        // 呼叫資料層(service)進行後續的檢查和查詢
        List<Map<String, Object>> items = voteService.fetchAllItems();
        // 回傳正常和結果(JDBC查詢結果符合response格式，不須再自行建構，直接回傳結果)
        return ResponseEntity.ok(items);
    }

    // 新增項目
    @PostMapping("/newitem")
    public ResponseEntity<Map<String, String>> addItem(@RequestBody Map<String, String> request){
        String name = request.get("itemname");
        voteService.additem(name);

        // 建構回傳訊息
        Map<String, String> response = new HashMap<>();
        response.put("message", "成功，新增名稱" + name);
        return ResponseEntity.ok(response);
    }

    // 刪除項目，DeteteMapping專門接收method = DELETE
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Map<String, String>> deleteItem(@PathVariable int id){
        voteService.deleteItem(id);

        // 建構回傳訊息
        Map<String, String> response = new HashMap<>();
        response.put("message", "成功刪除");
        return ResponseEntity.ok(response);
    } 

    // 查詢所有項目票數
    @GetMapping("/summary")
    public ResponseEntity<List<Map<String, Object>>> selectItemSummary(){
        List<Map<String, Object>> result = voteService.selectItemSummary();
        // 回傳正常和結果(JDBC查詢結果符合response格式，不須再自行建構，直接回傳結果)
        return ResponseEntity.ok(result);
    }

    // 新增投票紀錄
    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submit(@RequestBody Map<String, Object> request){
        // 取得對應欄位的資料
        String voter = (String) request.get("voter");
        List<Integer> idList = (List<Integer>) request.get("itemids");
        voteService.submit(voter, idList);

        // 建構回傳訊息 
        Map<String, String> response = new HashMap<>();
        response.put("message", "新增記錄成功");
        return ResponseEntity.ok(response);
    }
}
