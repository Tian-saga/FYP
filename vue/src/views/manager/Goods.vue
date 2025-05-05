<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.name" style="width: 300px; margin-right: 10px" placeholder="Please enter a name to enquire"></el-input>
      <el-button type="primary" @click="onSearch">Inquire</el-button>
      <el-button type="info" style="margin: 0 10px" @click="onReset">Reset</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">Add</el-button>
      </div>
      <el-table :data="data.tableData" stripe>
        <el-table-column label="name" prop="name"></el-table-column>
        <el-table-column label="image" prop="img">
          <template #default="scope">
            <el-image style="width: 50px; height: 50px; border-radius:5px" :src="scope.row.img"
                      :preview-src-list="[scope.row.img]" preview-teleported>

            </el-image>
          </template>
        </el-table-column>
        <el-table-column label="description" prop="descr"></el-table-column>
        <el-table-column label="features" prop="specials"></el-table-column>
        <el-table-column label="price" prop="price"></el-table-column>
        <el-table-column label="unit" prop="unit"></el-table-column>
        <el-table-column label="Inventory" prop="store"></el-table-column>
        <el-table-column label="classify" prop="categoryName"></el-table-column>
        <el-table-column label="operate" header-align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">edit</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">delete</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <el-pagination
          background
          :current-page="data.pageNum"
          :page-size="data.pageSize"
          :total="data.total"
          layout="sizes, prev, pager, next, total"
          :page-sizes="[8,16,20]"
          @current-change="onPageChange"
          @size-change="onSizeChange"/>
    </div>

    <el-dialog title="ProductInfo" width="40%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="name" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="image" prop="img">
          <el-upload :action="uploadUrl" list-type="picture" :on-success="handleImgSuccess">
            <el-button type="primary">upload image</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="description" prop="descr">
          <el-input type="textarea" v-model="data.form.descr" autocomplete="off" />
        </el-form-item>
        <el-form-item label="features" prop="specials">
          <el-input v-model="data.form.specials" autocomplete="off" />
        </el-form-item>
        <el-form-item label="price" prop="price">
          <el-input v-model="data.form.price" autocomplete="off" />
        </el-form-item>
        <el-form-item label="unit" prop="unit">
          <el-input v-model="data.form.unit" autocomplete="off" />
        </el-form-item>
        <el-form-item label="Inventory" prop="store">
          <el-input v-model="data.form.store" autocomplete="off" />
        </el-form-item>
        <el-form-item label="classify" prop="categoryId">
          <el-select v-model="data.form.categoryId" placeholder="Please select a category" style="width: 100%">
            <el-option
                v-for="item in data.categoryList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            />
          </el-select>
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
import { reactive, onMounted } from "vue";
import { ElMessageBox, ElMessage } from "element-plus";



// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + '/files/upload'

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  pageNum: 1,
  pageSize: 8,
  total: 0,
  formVisible: false,
  form: {},
  tableData: [],
  name: null,
  categoryList: []
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

// ① 组件挂载后，自动拉一次第一页
onMounted(() => {
  load()
})

// ② 页码 改变时
function onPageChange(newPage) {
  data.pageNum = newPage
  load()
}
// ③ 每页大小 改变时
function onSizeChange(newSize) {
  data.pageSize = newSize
  data.pageNum  = 1
  load()
}

// ④ 搜索按钮点击
function onSearch() {
  data.pageNum = 1
  load()
}
// ⑤ 重置按钮点击
function onReset() {
  data.name    = null
  data.pageNum = 1
  load()
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
  request.post('/goods/add', data.form).then(res => {
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
  request.put('/goods/update', data.form).then(res => {
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
    request.delete('/goods/delete/' + id).then(res => {
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
  data.form.img = res.data  // res.data就是文件上传返回的文件路径，获取到路径后赋值表单的属性
}

load()
</script>