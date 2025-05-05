<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.name" style="width: 300px; margin-right: 10px" placeholder="Please enter a name to enquire"></el-input>
      <el-button type="primary" @click="loadAdmins">Inquire</el-button>
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
            <span v-if="scope.row.role === 'ADMIN'">administrator</span>
          </template>
        </el-table-column>
        <el-table-column label="operate" align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">edit</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card">
      <el-pagination
          background
          layout="sizes, prev, pager, next, total"
          :page-sizes="[5, 10, 20, 50]"
          :current-page="data.pageNum"
          :page-size="data.pageSize"
          :total="data.total"
          @current-change="onPageChange"
          @size-change="onSizeChange"
      />
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
import { reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessageBox, ElMessage } from 'element-plus'

// 文件上传的接口（如果有上传）
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  // 搜索框要搜 username
  username:    null,

  // 分页
  pageNum:     1,
  pageSize:    10,
  total:       0,

  // 表格和弹窗
  tableData:   [],
  formVisible: false,
  form:        {}
})

// —— 1) 拉第一页 / 搜索 & 翻页 & 改页大小 都用同一个方法 ——
function loadAdmins() {
  request.get('/admin/selectPage', {
    params: {
      pageNum:  data.pageNum,
      pageSize: data.pageSize,
      username: data.username
    }
  }).then(res => {
    data.tableData = res.data.list
    data.total     = res.data.total
  })
}

// 初始挂载和搜索按钮都调用它
onMounted(loadAdmins)

// 翻页回调
function onPageChange(newPage) {
  data.pageNum = newPage
  loadAdmins()
}

// 修改每页大小的回调
function onSizeChange(newSize) {
  data.pageSize = newSize
  data.pageNum  = 1      // 回第一页
  loadAdmins()
}

// 搜索框右边的 “Reset” 按钮
function reset() {
  data.username = null
  loadAdmins()
}

// —— 弹窗相关：Add / Edit / Save / Delete ——

// 点击 “Add”
function handleAdd() {
  data.form = {}
  data.formVisible = true
}

// 点击 “Edit”
function handleEdit(row) {
  data.form = { ...row }
  data.formVisible = true
}

// 新增保存
function add() {
  request.post('/admin/add', data.form).then(res => {
    if (res.code === '200') {
      loadAdmins()
      ElMessage.success('Added!')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 编辑保存
function update() {
  request.put('/admin/update', data.form).then(res => {
    if (res.code === '200') {
      loadAdmins()
      ElMessage.success('Updated!')
      data.formVisible = false
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 弹窗的 “Save” 按钮
function save() {
  data.form.id ? update() : add()
}

// 删除
function handleDelete(id) {
  ElMessageBox.confirm(
      'This cannot be undone. Delete anyway?',
      'Warning',
      { type: 'warning' }
  ).then(() => {
    request.delete(`/admin/delete/${id}`).then(res => {
      if (res.code === '200') {
        loadAdmins()
        ElMessage.success('Deleted!')
      } else {
        ElMessage.error(res.msg)
      }
    })
  })
}

// 弹窗的 “Cancel” 按钮
function resetForm() {
  data.form = {}
  data.formVisible = false
}
</script>
