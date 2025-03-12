
<template>
  <div>
  <div class="card"></div>
  <el-button v-for="item in data.categoryList" :key="item.id">{{item.name}}</el-button>
  </div>
</template>


<script setup>
import { reactive } from "vue";
import request from "@/utils/request";

const data = reactive({
  categoryList: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  goodsList: [],
  name: ''
    })


//获取分类数据
request.get('/category/selectAll').then(res =>{
  data.categoryList =res.data
})

// 分页查询
const load = () => {
  request.get('/goods/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
}
</script>

<style >

</style>