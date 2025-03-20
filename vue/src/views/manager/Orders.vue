<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.orderNo" style="width: 300px; margin-right: 10px" placeholder="Please enter a order number to enquire"></el-input>
      <el-button type="primary" @click="load">Inquire</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">Reset</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <el-table :data="data.tableData" stripe>
        <el-table-column label="orderNo" prop="orderNo"></el-table-column>
        <el-table-column label="ProductName" prop="goodsName"></el-table-column>
        <el-table-column label="ProductImg" prop="goodsImg">

          <template #default="scope">
            <el-image style="width: 50px; height: 50px; border-radius:5px" :src="scope.row.goodsImg"
                      :preview-src-list="[scope.row.goodsImg]" preview-teleported>

            </el-image>
          </template>

        </el-table-column>


        <el-table-column label="number" prop="num"></el-table-column>
        <el-table-column label="customer" prop="userName"></el-table-column>
        <el-table-column label="status" prop="status">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'cancelled'" type="danger">cancelled</el-tag>
            <el-tag v-if="scope.row.status === 'Awaiting Payment'" type="warning">Awaiting Payment</el-tag>
            <el-tag v-if="scope.row.status === 'Awaiting Shipment'" type="primary">Awaiting Shipment</el-tag>
            <el-tag v-if="scope.row.status === 'Awaiting Receipt'" type="primary">Awaiting Receipt</el-tag>
            <el-tag v-if="scope.row.status === 'Completed'" type="success">Completed</el-tag>
          </template>

        </el-table-column>
        <el-table-column label="OrderTime" prop="time"></el-table-column>
        <el-table-column label="operate" width="160">
          <template #default="scope">
            <el-button v-if="data.user.role === 'USER' && scope.row.status === 'Awaiting Payment'" size="small" type="primary" @click="changeStatus(scope.row,'Awaiting Shipment')">pay</el-button>
            <el-button v-if="data.user.role === 'ADMIN' && scope.row.status === 'Awaiting Shipment'" size="small" type="primary" @click="changeStatus(scope.row,'Awaiting Receipt')">dispatch</el-button>
            <el-button v-if="data.user.role === 'USER' && scope.row.status === 'Awaiting Receipt'" size="small" type="success" @click="changeStatus(scope.row,'Completed')">receive</el-button>
            <el-button v-if="data.user.role === 'USER' && scope.row.status === 'Awaiting Payment'" size="small" type="danger" @click="changeStatus(scope.row,'cancelled')">cancel</el-button>
            <el-button v-if="data.user.role === 'ADMIN'" size="small" type="danger" @click="handleDelete(scope.row.id)">delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive} from "vue";
import {ElMessageBox, ElMessage} from "element-plus";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  orderNo: null
})

// 分页查询
const load = () => {
  request.get('/orders/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      orderNo: data.orderNo
    }
  }).then(res => {
    data.tableData = res.data?.list
    data.total = res.data?.total
  })
}




// 编辑保存
const changeStatus = (row, status) => {
  row.status = status
  request.put('/orders/update', row).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('Operate Successfully')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}


// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('The data cannot be recovered after deletion, are you sure to delete it?', 'Delete confirmation', { type: 'warning' }).then(res => {
    request.delete('/orders/delete/' + id).then(res => {
      if (res.code === '200') {
        load()
        ElMessage.success('Operate Successfully')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {})
}

// 重置
const reset = () => {
  data.orderNo = null
  load()
}

// 处理文件上传的钩子
const handleImgSuccess = (res) => {
  data.form.avatar = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
}

load()
</script>