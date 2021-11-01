<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">后台管理系统</div>
            <el-form :model="admin" :rules="rules" ref="loginForm" label-width="0px" class="ms-content">
                <el-form-item prop="userId">
                    <el-input v-model="admin.userId" placeholder="请输入账号">
                        <template #prepend>
                            <el-button icon="el-icon-user"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" placeholder="请输入密码" v-model="admin.password">
                        <template #prepend>
                            <el-button icon="el-icon-lock"></el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="submitForm('loginForm')">登录</el-button>
                </div>
                <p class="login-tips">
                    <label style="float:left;">Tips : 欢迎登录</label>
                    <label style="float:right;letter-spacing: 1px;cursor: pointer;">注册</label>
                </p>
            </el-form>
        </div>
    </div>
</template>

<script>
    import { login } from "../api/user";
    import {mapMutations} from "vuex";
    import md5 from 'js-md5';

    export default {
        data() {
            return {
                admin:{
                    userId: '',
                    password: ''
                },
                rules: {
                    userId: [
                        { required: true, message: '请输入账号', trigger: 'blur' },
                        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
                    ],
                    password: [
                        { required: true, message: '请输入密码', trigger: 'blur' },
                        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
                    ],
                }
            }
        },
        created(){
            //this.onLoad();
        },
        watch: {

        },
        computed:{

        },
        mounted(){

        },
        methods: {
            ...mapMutations(['changeLogin']),
            submitForm(formName){
                let v=this;
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        //对密码进行加密
                        let salt = "1a2b3c"
                        login({userId: this.admin.userId, password: md5(salt.charAt(0) + this.admin.password + salt.charAt(1) + salt.charAt(salt.length-1))}).then((res) => {
                            this.$message.error(res.info);
                            console.log(res)
                            if(res.result){
                                v.userToken = res.data.token;
                                // 将用户token保存到vuex中
                                v.changeLogin({ Authorization: v.userToken });
                                v.$router.push('/dashboard');
                            }
                        });
                    } else {
                        console.log('请输入正确的格式');
                        return false;
                    }
                });
            },
        }
    };
</script>

<style scoped>
    .login-wrap {
        position: relative;
        width: 100%;
        height: 100%;
        background-image: url(../assets/img/login-bg.jpg);
        background-size: 100%;
    }
    .ms-title {
        width: 100%;
        line-height: 50px;
        text-align: center;
        font-size: 20px;
        color: #fff;
        border-bottom: 1px solid #ddd;
    }
    .ms-login {
        position: absolute;
        left: 50%;
        top: 50%;
        width: 350px;
        margin: -190px 0 0 -175px;
        border-radius: 5px;
        background: rgba(255, 255, 255, 0.3);
        overflow: hidden;
    }
    .ms-content {
        padding: 30px 30px;
    }
    .login-btn {
        text-align: center;
    }
    .login-btn button {
        width: 100%;
        height: 36px;
        margin-bottom: 10px;
    }
    .login-tips {
        width: 100%;
        height: 30px;
        font-size: 12px;
        line-height: 30px;
        color: #fff;
    }
</style>