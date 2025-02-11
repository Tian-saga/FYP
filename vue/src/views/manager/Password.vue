<template>
  <div style="width: 50%">
    <div class="card" style="padding: 30px">
      <el-form :model="data.user" label-width="100px" style="padding-right: 50px">
        <el-form-item label="OriginPassword">
          <el-input v-model="data.user.password" show-password />
        </el-form-item>
        <el-form-item label="NewPassword">
          <el-input v-model="data.user.newPassword" show-password />
        </el-form-item>
        <el-form-item label="NewPassword">
          <el-input v-model="data.user.confirmPasword" show-password />
        </el-form-item>
        <div style="text-align: center">
          <el-button type="primary" @click="save">Save</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {reactive} from "vue"
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import router from "@/router";

const data = reactive({
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
})

// 把当前修改的用户信息存储到后台数据库
const save = () => {
  if (data.user.newPassword !== data.user.confirmPasword) {
    ElMessage.error('The new password is incorrect')
    return
  }
  request.put('/updatePassword', data.user).then(res => {
    if (res.code === '200') {
      ElMessage.success('Change password successfully')
      //把更新后的用户信息存储到缓存
      localStorage.setItem('system-user', JSON.stringify(data.user))
      router.push('/login')
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>