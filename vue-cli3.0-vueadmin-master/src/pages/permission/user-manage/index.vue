<template lang="html">
  <el-card class="box-card">
    <div class="search-bar">
      <el-form :inline="true" :model="searchData" class="fl">
        <el-input style="display: none;"></el-input>
        <el-form-item label="登录名称">
          <el-input v-model="searchData.loginName" placeholder="登录名称" @keyup.enter.native="onSearch()"></el-input>
        </el-form-item>
      </el-form>
      <div class="fl">
        <el-button type="text" @click="handleReset">重置</el-button>
        <el-button type="primary" icon="el-icon-search" @click="onSearch">查询</el-button>
      </div>
    </div>
    <div  class="tools-bar">
      <el-button type="success" icon="el-icon-plus" size="small" @click="dialogVisible = true;dialogTitle='新增用户'">新增用户</el-button>
    </div>
    <div>
      <el-table
        v-loading.body="tableLoading"
        ref="singleTable"
        :data="tableData"
        border
        highlight-current-row
        style="width: 100%">
        <el-table-column
          type="index"
          width="60">
        </el-table-column>
        <el-table-column
          prop="state"
          label="停用/启用"
          align="center"
          min-width="100">
          <template slot-scope="scope">
            <!-- <el-tag v-if="scope.row.status=='1'" color="#13CE66">启用</el-tag>
            <el-tag v-if="scope.row.status=='0'" color="#FF4949">停用</el-tag> -->
            <el-switch
              v-model="scope.row.state"
              :active-value="1"
              :inactive-value="0"
              active-text=""
              inactive-text=""
              @change="handleStatus(scope.row)">
            </el-switch>
          </template>
        </el-table-column>
        <el-table-column
          prop="username"
          label="登录账号"
          min-width="120">
        </el-table-column>
        <el-table-column
          prop="nickname"
          label="昵称"
          min-width="120">
        </el-table-column>
        <el-table-column
          prop="email"
          label="邮箱"
          width="130">
        </el-table-column>
        <el-table-column
          prop="roleName"
          label="角色"
          min-width="120">
        </el-table-column>
        <el-table-column
          prop="lastLoginTime"
          label="最后登录时间"
          :formatter="toDateTime"
          width="180">
        </el-table-column>
        <el-table-column
          label="操作"
          fixed="right"
          width="170">
          <template slot-scope="scope">
  <div>
    <el-button type="text" size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
    <el-button type="text" size="small" @click="handleResetPwd(scope.$index, scope.row)">重置密码</el-button>
    <el-button type="text" size="small" @click="handleDel(scope.$index, scope.row)" style="color:red;">删除</el-button>
  </div>
</template>
        </el-table-column>
      </el-table>
      <div class="pagination-bar">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :page-sizes="[10, 25, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalRecord">
        </el-pagination>
      </div>
    </div>
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" @close="onDialogClose()">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" label-width="80px">
        <!--<el-form-item label="登录名" prop="loginName">
          <template v-if="dialogTitle=='修改用户信息'">{{dataForm.loginName}}</template>
          <el-input v-else v-model="dataForm.loginName" placeholder="登录名"></el-input>
        </el-form-item>-->
        <el-form-item label="登录账号" prop="username">
            <template v-if="dialogTitle=='修改用户信息'">{{dataForm.username}}</template>
              <el-input v-model="dataForm.username" placeholder="请输入用户登录账号"></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="dataForm.nickname" placeholder="请输入用户昵称"></el-input>
            </el-form-item>
            <el-form-item label="用户角色" prop="roleId">
              <el-select v-model="dataForm.roleId" placeholder="请选择角色" style="width: 100%;">
                  <el-option
                      v-for="item in rolesDic"
                      :key="item.id"
                      :label="item.roleName"
                      :value="item.id">
                  </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="dataForm.email" placeholder="电子邮箱"></el-input>
            </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="info" @click="onDialogSubmit()" v-if="dialogTitle=='修改用户信息'">保存</el-button>
        <el-button type="primary" @click="onDialogSubmit('dataForm')" v-else>立即创建</el-button>
      </div>
    </el-dialog>
  </el-card>
</template>
<script>
import { getUserList, getRoleList } from '@/api/permission'
import { add, update, updatePassword, remove } from '@/api/user'
import {encryptWebPassword} from '@/utils/md5-util'
import moment from 'moment'
export default {
    data() {
        return {
            totalRecord: 0,
            pageSize: 10,
            tableLoading: false,
            dialogVisible: false,
            dialogTitle: '新增用户',
            roles: [
                {
                    id: 1,
                    roleName: '超级管理员'
                },
                {
                    id: 2,
                    roleName: '普通用户'
                }
            ],
            defaultTreeProps: {
                children: 'childPermList',
                label: 'permissionName'
            },
            rules: {
                username: [
                    {
                        required: true,
                        message: '用户登录账号不能为空',
                        trigger: 'blur'
                    },
                    {
                        min: 5,
                        max: 30,
                        message: '长度在 5 到 30 个字符',
                        trigger: 'blur'
                    }
                ],
                roleId: [
                    { required: true, message: '请选择用户角色', trigger: 'change' }
                ],
            },
            searchData: {
                loginName: ''
            },
            defaultPassword: '123456',
            dataForm: {
                id: '',
                username: '',
                nickname: '',
                password: '',
                email: '',
                roleId: '',
            },
            rolesDic: [],
            tableData: []
        }
    },
    created() {
        this.init()
    },
    methods: {
        async init() {
            //准备角色字典
            const roles =  await getRoleList()
            this.rolesDic = roles.data

            const res = await getUserList()
            this.tableData = res.data
        },
        handleStatus(row) {},
        statusFormat(row, column, cellValue) {
            return { '0': '停用', '1': '启用' }[cellValue] || ''
        },
        onDialogClose() {
            this.dataForm.tempRoleIds = []
            this.$refs.dataForm.resetFields()
        },
        handleSizeChange(val) {
            this.pageSize = val
            this.onSearch()
        },
        handleCurrentChange(val) {
            this.onSearch({ pageNumber: val })
        },
        handleReset() {
            this.searchData = {
                loginName: ''
            }
            this.onSearch()
        },
        onSearch({ pageNumber = 1 } = {}) {},
        toDateTime(row, column, cellValue) {
            return cellValue
                ? moment(cellValue).format('YYYY-MM-DD HH:mm:ss')
                : ''
        },
        roleFormatter(row, column, cellValue) {
            let result = []
            if (typeof row.erpMemberRoles === 'object' && row.erpMemberRoles.length > 0) {
                for (let item of row.erpMemberRoles) {
                    result.push(item.roleName)
                }
            }
            return result.join('，')
        },
        handleChangeStatus(index, row) {},
        handleResetPwd(index, row) {
            this.$confirm('确认重置该用户的登录密码？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //重置密码
                row.password = encryptWebPassword(this.defaultPassword)
                this.updateUserPassword(row)
            })
        },
        handleDel(index, row) {
            this.$confirm('确认删除该用户信息？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //删除用户
                this.removeUser(row)
            })
        },
        handleEdit(index, row) {
            this.dialogVisible = true
            this.dialogTitle = '修改用户信息'
            this.dataForm.tempRoleIds = []
            for (let x of Object.keys(this.dataForm)) {
                if (
                    x === 'tempRoleIds' &&
                    typeof row.roleList === 'object' &&
                    row.roleList.length > 0
                ) {
                    for (let item of row.roleList) {
                        this.dataForm.tempRoleIds.push(item.id)
                    }
                } else {
                    this.dataForm[x] = row[x]
                }
            }
        },
        async onDialogSubmit(formName) {
             this.$refs[formName].validate((valid) => {
                if (valid) {
                    //校验成功，准备提交后台
                    //默认初始密码
                    this.dataForm.password = encryptWebPassword(this.defaultPassword)
                    this.createUser(this.dataForm)
                } else {
                    console.log('error submit!!')
                    return false;
                }
            })
        },
        async createUser(user){
            console.log(user)
            const res = await add(user)
            if(res.result == 1){
                this.$message({
                    message: '用户创建成功',
                    type: 'success'
                });
                this.init()
            }else{
                this.$message({
                    message: '用户创建失败',
                    type: 'error'
                });
            }
            this.dialogVisible = false
        },
        async updateUser(user){
            const res = await update(user)
            console.log(res)
            if(res.result == 1 && res.data == true){
                this.$message({
                    message: '修改成功',
                    type: 'success'
                });
                this.init()
            }else{
                this.$message({
                    message: '修改失败',
                    type: 'error'
                });
            }
            this.dialogVisible = false
        },
        async updateUserPassword(user){
            const res = await updatePassword(user)
            console.log(res)
            if(res.result == 1 && res.data == true){
                this.$message({
                    message: '重置成功',
                    type: 'success'
                });
                this.init()
            }else{
                this.$message({
                    message: '重置失败,当前密码为初始密码',
                    type: 'warning'
                });
            }
        },
        async removeUser(user){
            const res = await remove(user)
            if(res.result == 1 && res.data == true){
                this.$message({
                    message: '删除成功',
                    type: 'success'
                });
                this.init()
            }else{
                this.$message({
                    message: '删除失败',
                    type: 'error'
                });
            }
        }
    }
}
</script>

<style lang="scss">
.fr{
    float:right;
}
.fl{
    float:left;
}
.search-bar{
    overflow: hidden;
}
</style>

<style>
.tools-bar{
  margin-bottom:20px;
}
</style>
