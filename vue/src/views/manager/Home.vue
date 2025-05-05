<template>
  <div>
<div style="padding-left: 60px;font-size: 20px;font-weight:bold;margin-bottom: 20px">System Announcement </div>
    <el-timeline style="width: 50%">
      <el-timeline-item :timestamp="item.time" placement="top" v-for="item in data.noticeList" :key="item.id">

        <div class="card">
          <h4>{{ item.title }}</h4>
          <p>{{ item.content}}</p>
        </div>
      </el-timeline-item>
      </el-timeline>

    <div style="margin-top:20px">
      <el-pagination
          background
          layout="sizes, prev, pager, next, total"
          :page-sizes="[4,8,12]"
          :current-page="data.pageNum"
          :page-size="data.pageSize"
          :total="data.total"
          @current-change="onPageChange"
          @size-change="onSizeChange"
      />
    </div>

  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from "@/utils/request";

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  noticeList: [],    // 当前页的公告数组
  pageNum:     1,    // 当前页码
  pageSize:    4,    // 每页条数，默认 5
  total:       0     // 后端返回的总条数
})

function loadNotices() {
  request.get('/notice/selectPage', {
    params: {
      pageNum:  data.pageNum,
      pageSize: data.pageSize
    }
  }).then(res => {
    data.noticeList = res.data.list
    data.total      = res.data.total
  })
}

onMounted(() => {
  loadNotices()
})

function onPageChange(newPage) {
  data.pageNum = newPage
  loadNotices()
}

function onSizeChange(newSize) {
  data.pageSize = newSize
  data.pageNum  = 1
  loadNotices()
}


request.get('/notice/selectAll').then(res =>{
  data.noticeList=res.data
})
</script>