<template>
  <div class="app-container">
    <div class="admin-container">
      <h2>1. 後台管理 - 投票項目編輯</h2>
      <div class="input-group">
        <input v-model="newItemName" placeholder="請輸入欲新增的投票項目名稱" />
        <button class="btn-add" @click="AddItem">新增項目</button>
      </div>
      <hr class="divider" />
      <h3>目前所有投票項目</h3>
      <table class="item-table">
        <thead>
          <tr>
            <th>項目 ID</th>
            <th>項目名稱 (Item)</th>
            <th style="width: 100px; text-align: center;">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in voteItems" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.Item }}</td>
            <td style="text-align: center;">
              <button class="btn-delete" @click="DeleteItem(item.id, item.Item)">❌ 刪除</button>
            </td>
          </tr>
          <tr v-if="voteItems.length === 0">
            <td colspan="3" style="text-align: center; color: #999;">目前暫無任何投票項目</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="client-container">
      <h2>2. 前台畫面 - 累積投票結果與線上投票</h2>
      
      <div class="summary-list">
        <label v-for="post in votingSummary" :key="post.id" class="summary-card">
          <div class="card-info">
            <input 
              type="checkbox" 
              :value="post.id" 
              v-model="selectedItemIds"
              class="vote-checkbox"
            />
            <span class="item-id">ID: {{ post.id }}</span>
            <span class="item-name">{{ post.Item }}</span>
          </div>
          <div class="card-count">
            累積票數：<strong>{{ post.vote_count }}</strong> 票
          </div>
        </label>
        
        <div v-if="votingSummary.length === 0" style="text-align: center; color: #999; width: 100%;">
          目前沒有任何可投票的項目
        </div>
      </div>

      <div v-if="votingSummary.length > 0" class="vote-action-zone">
        <hr class="divider" />
        <h3>填寫資料送出投票</h3>
        <div class="vote-form">
          <input 
            v-model="voterName" 
            placeholder="請輸入您的姓名（限純文字，防範 XSS 攻擊）" 
            class="voter-input"
          />
          <button class="btn-submit" @click="VoteSubmit">送出多選投票</button>
        </div>
        <p class="selected-hint">已選取項目 ID: {{ selectedItemIds }}</p>
      </div>

    </div>
  </div>
</template>

<script setup>
    import { onMounted, ref } from 'vue';

    const newItemName = ref('');
    // 所有投票項目，陣列型態
    const voteItems = ref([]);
    const votingSummary = ref([]);

    // 前台投票專用雙向綁定變數
    const voterName = ref('');
    const selectedItemIds = ref([]); // 陣列型態，會自動收集所有被勾選的 ID

    // 查詢所有項目
    async function SelectAllItem(){
        try{
            // 連線後端
            const response = await fetch ('http://localhost:8080/api/votes/allitems');

            // 接收後端的回傳並檢查是否有錯誤
            if (response.ok){
                // 解析json資料
                const result = await response.json();
                voteItems.value = result;
            }
        }
        catch(error){
            alert('錯誤:' + error);
        }
    }

    // 新增投票類別
    async function AddItem(){
        // 避免不輸入就直接按新增
        if (!newItemName.value.trim()){
            alert('請輸入資料');
            return;
        }
        try{
            // 建構post訊息給後端，body內容包含新的項目名稱
            const response = await fetch('http://localhost:8080/api/votes/newitem', {
                method: 'POST',
                headers: { 'Content-Type':'application/json' },
                body:JSON.stringify({ itemname:newItemName.value })
            });

            // 接收後端的回傳並檢查是否有錯誤
            if (response.ok){
                alert('成功');
                newItemName.value = '';
                
                // 新增完畢再查詢一遍所有項目和票數做刷新
                await SelectAllItem();
                await SelectItemSummary();
            }
        }
        catch (error){
            alert('錯誤:' + error);
        }
    }

    // 刪除特定項目
    async function DeleteItem(id, name){
        if (!confirm('確定刪除' + name)) return;

        try{
            const response = await fetch(`http://localhost:8080/api/votes/item/${id}`, {method:'DELETE'});

            // 接收後端的回傳並檢查是否有錯誤
            if (response.ok){
                alert('刪除成功');

                // 新增完畢再查詢一遍所有項目和票數做刷新
                await SelectAllItem();
                await SelectItemSummary();
            }
        }
        catch (error){
            alert('錯誤:' + error);
        }
    }

    // 查詢所有項目的票數
    async function SelectItemSummary(){
        try{
            const response = await fetch('http://localhost:8080/api/votes/summary');

            // 接收後端的回傳並檢查是否有錯誤
            if (response.ok){
                const result = await response.json();
                votingSummary.value = result;
            }
        }
        catch (error){
            alert('錯誤:' + error);
        }
    }

    // 新增投票紀錄
    async function VoteSubmit(){
        if (!voterName.value.trim()) {
            alert("姓名不能為空值");
            return;
        }
        if (selectedItemIds.value.length === 0) {
            alert("至少要選一項");
            return;
        }

        try{
            const response = await fetch('http://localhost:8080/api/votes/submit', {
                method:'POST',
                headers:{ 'Content-Type' : 'application/json' },
                body: JSON.stringify({
                    voter:voterName.value,
                    itemids:selectedItemIds.value
                })
            });

            // 接收後端的回傳並檢查是否有錯誤
            if (response.ok){
                alert('投票成功');

                // 新增完畢再查詢一遍所有項目和票數做刷新
                await SelectAllItem();
                await SelectItemSummary();

                // 重製選項和姓名
                voterName.value = '';
                selectedItemIds.value = [];
            }
        }
        catch (error){
            alert('錯誤:' + error)
        }
    }

    
// 開網頁自動先執行查詢所有項目和各自票數
onMounted(() => {
    SelectAllItem();
    SelectItemSummary()
});

</script>

<style scoped>
.app-container { display: flex; flex-direction: column; gap: 30px; padding: 20px; font-family: sans-serif; }
.admin-container, .client-container { max-width: 650px; width: 100%; margin: 0 auto; padding: 25px; border: 1px solid #dee2e6; border-radius: 10px; background-color: #f8f9fa; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
.client-container { background-color: #eef2f7; border-color: #b0c4de; }
.summary-list { display: flex; flex-direction: column; gap: 12px; margin-top: 15px; }
.summary-card { display: flex; justify-content: space-between; align-items: center; padding: 15px; background-color: white; border-radius: 6px; border-left: 5px solid #0d6efd; box-shadow: 0 2px 4px rgba(0,0,0,0.02); cursor: pointer; }
.summary-card:hover { background-color: #fdfdfd; }
.card-info { display: flex; gap: 15px; align-items: center; }
.vote-checkbox { width: 18px; height: 18px; cursor: pointer; }
.item-id { color: #6c757d; font-size: 13px; }
.item-name { font-weight: bold; font-size: 16px; }
.card-count strong { color: #dc3545; font-size: 18px; }
.vote-action-zone { margin-top: 20px; }
.vote-form { display: flex; gap: 12px; margin-top: 10px; }
.voter-input { flex: 1; padding: 10px; border: 1px solid #ced4da; border-radius: 5px; }
.btn-submit { background-color: #198754; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
.btn-submit:hover { background-color: #157347; }
.selected-hint { font-size: 12px; color: #6c757d; margin-top: 8px; }
.input-group { display: flex; gap: 12px; margin-top: 15px; }
input { padding: 10px; font-size: 14px; border: 1px solid #ced4da; border-radius: 5px; }
button { border: none; border-radius: 5px; cursor: pointer; font-weight: bold; }
.btn-add { background-color: #0d6efd; color: white; padding: 10px 15px; }
.btn-delete { padding: 6px 12px; background-color: #dc3545; color: white; font-size: 12px; }
.divider { margin: 25px 0; border: 0; border-top: 1px solid #ced4da; }
.item-table { width: 100%; border-collapse: collapse; margin-top: 15px; background-color: white; }
.item-table th, .item-table td { border: 1px solid #dee2e6; padding: 12px; text-align: left; }
.item-table th { background-color: #e9ecef; color: #495057; }
</style>