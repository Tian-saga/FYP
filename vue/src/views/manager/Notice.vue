<template>
  <div>

    <div class="card" style="margin-bottom: 5px;">
      <el-input v-model="data.title" style="width: 300px; margin-right: 10px" placeholder="Please enter a title to enquire"></el-input>
      <el-button type="primary" @click="load">Inquire</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">Reset</el-button>
    </div>

    <div class="card" style="margin-bottom: 5px">
      <div style="margin-bottom: 10px">
        <el-button type="primary" @click="handleAdd">Add</el-button>
      </div>
      <el-table :data="data.noticeList" stripe>
        <el-table-column label="title" prop="title"></el-table-column>
        <el-table-column label="content" prop="content"></el-table-column>
        <el-table-column label="time" prop="time"></el-table-column>
        <el-table-column label="operate" header-align="center" width="160">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)">edit</el-button>
            <el-button type="danger" @click="handleDelete(scope.row.id)">delete</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:20px; text-align:right">
         <el-pagination
              background
              layout="sizes, prev, pager, next, total"
              :page-sizes="[5,10,20,50]"
              :current-page="data.pageNum"
              :page-size="data.pageSize"
              :total="data.total"
              @current-change="onPageChange"
              @size-change="onSizeChange"
           />
        </div>


    </div>

    <div class="card" v-if="data.total">
      <el-pagination background layout="prev, pager, next" v-model:page-size="data.pageSize" v-model:current-page="data.pageNum" :total="data.total"/>
    </div>

    <el-dialog title="Information" width="40%" v-model="data.formVisible" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="title" prop="title">
          <el-input v-model="data.form.title" autocomplete="off" />
        </el-form-item>
        <el-form-item label="content" prop="content">
          <el-input type="textarea" v-model="data.form.content" autocomplete="off" />
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

const data = reactive({
  noticeList: [],   // 可以继续叫 tableData，也可以叫 noticeList
  pageNum:     1,
  pageSize:   10,
  total:      0,
  title:      null,
  formVisible: false,
  form:        {}
});

// —— 1. 统一的 load() 函数 ——
function load() {
  request.get('/notice/selectPage', {
    params: {
      pageNum:  data.pageNum,
      pageSize: data.pageSize,
      title:    data.title
    }
  }).then(res => {
    data.noticeList = res.data.list;
    data.total      = res.data.total;
  });
}

// —— 2. 自动拉第一页 & 分页回调 ——
onMounted(() => {
  load();
});

function onPageChange(newPage) {
  data.pageNum = newPage;
  load();
}

function onSizeChange(newSize) {
  data.pageSize = newSize;
  data.pageNum  = 1;
  load();
}

// —— 3. 保留你原来的 reset ——
function reset() {
  data.title = null;
  data.pageNum = 1;
  load();
}

// —— 4. 保留你原来的新增/编辑/保存/删除 ——
function handleAdd() {
  data.form = {};
  data.formVisible = true;
}

function handleEdit(row) {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
}

function add() {
  request.post('/notice/add', data.form).then(res => {
    if (res.code === '200') {
      load();
      ElMessage.success('Operate Successfully');
      data.formVisible = false;
    } else {
      ElMessage.error(res.msg);
    }
  });
}

function update() {
  request.put('/notice/update', data.form).then(res => {
    if (res.code === '200') {
      load();
      ElMessage.success('Operate Successfully');
      data.formVisible = false;
    } else {
      ElMessage.error(res.msg);
    }
  });
}

function save() {
  data.form.id ? update() : add();
}

function handleDelete(id) {
  ElMessageBox.confirm(
      'The data cannot be recovered after deletion, are you sure to delete it?',
      'Delete confirmation',
      { type: 'warning' }
  ).then(() => {
    request.delete('/notice/delete/' + id).then(res => {
      if (res.code === '200') {
        load();
        ElMessage.success('Operate Successfully');
      } else {
        ElMessage.error(res.msg);
      }
    });
  });
}
</script>
