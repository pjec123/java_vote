// 業務層，負責進行從前端來的數據的安全檢查

package com.example.demo.service;
import com.example.demo.repository.VoteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    // 取得全部可投票項目
    @Transactional
    public List<Map<String, Object>> fetchAllItems(){
        return voteRepository.getAllItems();
    }

    // 新增項目
    @Transactional
    public void additem(String name){
        // 不接受紀錄空值
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("名稱不可為空值");
        }
        voteRepository.addItem(name);
    }

    // 刪除指定項目
    @Transactional
    public void deleteItem(int index){
        voteRepository.deleteItem(index);
    }

    // 查詢所有項目目前票數
    @Transactional
    public List<Map<String, Object>> selectItemSummary(){
        return voteRepository.selectItemSummary();
    }

    // 新增投票紀錄，使用transactional確保寫入是原子性的
    @Transactional
    public void submit(String voter, List<Integer> idList){
        // 不接受紀錄空值
        if (voter == null || voter.trim().isEmpty()){
            throw new IllegalArgumentException("姓名不可為空值");
        } 
        // 避免沒有選擇項目就提交
        if (idList == null || idList.isEmpty()){
            throw new IllegalArgumentException("至少要選一項");
        }

        // 用for迴圈的方式一筆一筆寫入
        for (Integer id : idList){
            voteRepository.submit(voter, id);
        }
    }
}