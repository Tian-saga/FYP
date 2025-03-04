<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.name" style="width: 300px; margin-right: 10px" placeholder="Please enter a name to enquire"></el-input>
      <el-button type="primary" @click="load">Inquire</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">Reset</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">Add</el-button>
      </div>
      <el-table :data="data.tableData" stripe>
        <el-table-column label="username" prop="username"></el-table-column>
        <el-table-column label="name" prop="name"></el-table-column>
        <el-table-column label="avatar">
          <template #default="scope">
            <el-image :src="scope.row.avatar" style="width: 40px; height: 40px; border-radius: 50%"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="role" prop="role">
          <template #default="scope">
            <span v-if="scope.row.role === 'USER'">User</span>
          </template>
        </el-table-column>
        <el-table-column label="sex" prop="sex"></el-table-column>
        <el-table-column label="phone" prop="phone"></el-table-column>
        <el-table-column label="email" prop="email"></el-table-column>
        <el-table-column label="operate" header-align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">edit</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card">
      <el-pagination background layout="prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog title="Information" width="40%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="avatar" prop="avatar">
          <el-upload :action="uploadUrl" list-type="picture" :on-success="handleImgSuccess">
            <el-button type="primary">Upload an image</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="account" prop="username">
          <el-input v-model="data.form.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="name" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="sex" prop="sex">
          <el-radio-group v-model="data.form.sex">
            <el-radio label="male"></el-radio>
            <el-radio label="female"></el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="phone" prop="phone">
          <el-input v-model="data.form.phone" autocomplete="off" />
        </el-form-item>
        <el-form-item label="email" prop="email">
          <el-input v-model="data.form.email" autocomplete="off" />
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Save</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/request";
import {reactive} from "vue";
import {ElMessageBox, ElMessage} from "element-plus";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  name: null
})

// 分页查询
const load = () => {
  request.get('/user/selectPage', {
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

// 新增
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}

// 编辑
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}

// 新增保存
const add = () => {
  request.post('/user/add', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('Operate Successfully')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 编辑保存
const update = () => {
  request.put('/user/update', data.form).then(res => {
    if (res.code === '200') {
      load()
      ElMessage.success('Operate Successfully')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 弹窗保存
const save = () => {
  // data.form有id就是更新，没有就是新增
  data.form.id ? update() : add()
}

// 删除
const handleDelete = (id) => {
  ElMessageBox.confirm('The data cannot be recovered after deletion, are you sure to delete it?', 'Delete confirmation', { type: 'warning' }).then(res => {
    request.delete('/user/delete/' + id).then(res => {
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
  data.name = null
  load()
}

// 处理文件上传的钩子
const handleImgSuccess = (res) => {
  data.form.avatar = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
}

load()
</script>