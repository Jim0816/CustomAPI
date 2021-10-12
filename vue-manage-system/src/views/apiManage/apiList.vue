<template>
    <div class="bg">
        <div class="page-left">
            <div class="page-left-top">
                <span class="page-left-top-search">
                    <el-input placeholder="搜索数据对象" v-model="searchText" style="height: 10px;">
                        <template v-slot:suffix="suffix">
                            <i class="el-icon-search" @click="submitSearch" style="float:right;margin-top:6px;margin-right:10px;font-size:18px;cursor:pointer;color:#2D8CF0;"></i>
                        </template>
                    </el-input>
                </span>
            </div>
            <div class="page-left-bottom">
                <el-menu style="width:100%;height:100%;letter-spacing: 1px;"
                         default-active="1"
                         class="el-menu-vertical-demo"
                         @open="handleOpen"
                         @close="handleClose"
                         background-color="#242f42"
                         text-color="#fff"
                         active-text-color="#ffd04b">

                    <el-submenu :index="index" v-for="item , index in tables">
                        <template v-slot:title="title">
                            <span class="item-content">
                                <span class="item-content-logo"><i style="float:left;" class="el-icon-coin"></i></span>
                                <span class="item-content-title">{{item.tableName}}</span>
                                <el-tooltip class="item" effect="dark" content="创建接口" placement="top-start">
                                  <span class="item-content-btn" @click="openAddApi(item.tableName)"><i style="float:left;width:100%;height:100%;" class="el-icon-plus"></i></span>
                                </el-tooltip>
                            </span>
                        </template>
                        <el-menu-item :index="apiIndex" v-for="api , apiIndex in item.apis" @click="openApiEditPage(item.tableName,api)" style="font-size: 11px;height: 30px;line-height: 30px;">{{apiIndex + 1}} - {{api.name}}</el-menu-item>
                    </el-submenu>
                </el-menu>
            </div>
        </div>
        <div class="page-right">
            <div v-if="page_index == 0" class="index-div">
                <div class="index-div-top">
                    <span class="index-div-top-item">
                        <span style="float:left;width:35%;height:100%;background-color:#2D8CF0;display:flex;justify-content: center;">
                            <img style="margin-top:30px;width:40px;height:40px;" src="../../assets/img/database-logo.png">
                        </span>
                        <span style="float:left;width:65%;height:100%;text-align: center;">
                            <p style="margin-top:23px;font-size: 28px;color:#2D8CF0;font-weight: bolder;">{{tables.length}}</p>
                            <p style="margin-top:3px;font-size: 15px;color:#999999;">数据对象数量</p>
                        </span>
                    </span>
                    <span class="index-div-top-item">
                        <span style="float:left;width:35%;height:100%;background-color:#64D572;display:flex;justify-content: center;">
                            <img style="margin-top:30px;width:40px;height:40px;" src="../../assets/img/api-logo.png">
                        </span>
                        <span style="float:left;width:65%;height:100%;text-align: center;">
                            <p style="margin-top:23px;font-size: 28px;color:#64D572;font-weight: bolder;">{{api_count}}</p>
                            <p style="margin-top:3px;font-size: 15px;color:#999999;">接口数量</p>
                        </span>
                    </span>
                    <span class="index-div-top-item">
                        <span style="float:left;width:35%;height:100%;background-color:#FEBD01;display:flex;justify-content: center;">
                            <img style="margin-top:30px;width:40px;height:40px;" src="../../assets/img/api-use.png">
                        </span>
                        <span style="float:left;width:65%;height:100%;text-align: center;">
                            <p style="margin-top:23px;font-size: 28px;color:#FEBD01;font-weight: bolder;">172</p>
                            <p style="margin-top:3px;font-size: 15px;color:#999999;">接口调用总次数</p>
                        </span>
                    </span>
                    <span class="index-div-top-item">
                        <span style="float:left;width:35%;height:100%;background-color:#F25E43;display:flex;justify-content: center;">
                            <img style="margin-top:30px;width:40px;height:40px;" src="../../assets/img/operater.png">
                        </span>
                        <span style="float:left;width:65%;height:100%;text-align: center;">
                            <p style="margin-top:23px;font-size: 28px;color:#F25E43;font-weight: bolder;">26</p>
                            <p style="margin-top:3px;font-size: 15px;color:#999999;">配置接口用户数量</p>
                        </span>
                    </span>
                </div>
            </div>
            <!--<div v-if="page_index == 1" class="table-div">
                <div class="table-div-container">
                    <div class="table-div-container-top">
                        <el-menu
                                :default-active="activeIndex2"
                                class="el-menu-demo"
                                mode="horizontal"
                                @select="handleSelect"
                                background-color="#242f42"
                                text-color="#fff"
                                active-text-color="#ffd04b">
                            <el-menu-item index="1" @click="page_table_index = 1">基本配置</el-menu-item>
                            <el-menu-item index="2" @click="page_table_index = 2">字段配置</el-menu-item>
                            <el-menu-item index="3" @click="page_table_index = 3">权限配置</el-menu-item>
                        </el-menu>
                    </div>
                    <div class="table-div-container-top-right">
                        <span class="table-div-container-top-right-home" @mousemove="homeShow=1" @mouseleave="homeShow=0">
                            <img v-if="homeShow == 0" style="width:100%;height:100%;" src="../../assets/img/home-none.png" @click="page_index=0;homeShow=0"/>
                            <img v-if="homeShow == 1" style="width:100%;height:100%;" src="../../assets/img/home-blue.png" @click="page_index=0;homeShow=0"/>
                        </span>
                    </div>
                    <div class="table-div-container-navigate">
                        <label style="float:left;" >用户 > 数据配置 > {{page_table_index == 1 ? '基本配置' : (page_table_index == 2 ? '字段配置' : '权限配置')}}</label>
                    </div>
                    <div class="table-div-container-bottom" v-if="page_table_index == 1">
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">数据名称</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">用户</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">数据标识</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">tb_user</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">数据描述</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">用于存储用户基本信息</label></span>
                        </span>
                    </div>

                    <div class="table-div-container-bottom" v-if="page_table_index == 2">
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">基本配置接口名称</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">按照姓名查询用户信息</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">操作类型</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">get</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">接口描述</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">用户可以按照姓名查询</label></span>
                        </span>
                    </div>

                    <div class="table-div-container-bottom" v-if="page_table_index == 3">
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">基本配置接口名称</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">按照姓名查询用户信息</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">操作类型</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">get</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">接口描述</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">用户可以按照姓名查询</label></span>
                        </span>
                    </div>
                </div>
            </div>-->
            <div v-if="page_index == 2" class="api-div">
                <div class="api-div-container">
                    <div class="api-div-container-top">
                        <el-menu
                                :default-active="activeIndex2"
                                style="height: 100%;"
                                class="el-menu-demo"
                                mode="horizontal"
                                @select="handleSelect"
                                background-color="#242f42"
                                text-color="#fff"
                                active-text-color="#ffd04b">
                            <el-menu-item index="1" @click="page_api_index = 1">基本配置</el-menu-item>
                            <el-menu-item index="2" @click="page_api_index = 2">条件配置</el-menu-item>
                            <el-menu-item index="3" @click="page_api_index = 3">结果配置</el-menu-item>
                            <el-menu-item index="4" @click="page_api_index = 4">权限配置</el-menu-item>
                            <el-menu-item index="5" @click="page_api_index = 5">模板配置</el-menu-item>
                        </el-menu>
                    </div>
                    <div class="api-div-container-top-right">
                        <span class="api-div-container-top-right-home" @mousemove="homeShow=1" @mouseleave="homeShow=0">
                            <img v-if="homeShow == 0" style="width:100%;height:100%;" src="../../assets/img/home-none.png" @click="page_index=0;homeShow=0"/>
                            <img v-if="homeShow == 1" style="width:100%;height:100%;" src="../../assets/img/home-blue.png" @click="page_index=0;homeShow=0"/>
                        </span>
                    </div>
                    <div class="api-div-container-navigate">
                        <label style="float:left;" ><el-tag effect="dark" type="primary" size="mini">{{navigateBar.table}}</el-tag> >
                            <el-tag effect="dark" type="primary" size="mini">{{navigateBar.api}}</el-tag> >
                            <el-tag effect="dark" type="primary" size="mini">{{page_api_index == 1 ? '基本配置' : (page_api_index == 2 ? '条件配置' : (page_api_index == 3 ? '结果配置' : (page_api_index == 4 ? '权限配置' : '模板配置')))}}</el-tag>
                        </label>
                    </div>
                    <!--接口配置 -> 基本配置-->
                    <div v-if="page_api_index == 1" class="api-div-container-bottom">
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">基本配置接口名称</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">{{apiObj.name}}</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">操作类型</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">{{apiObj.require.operate}}</label></span>
                        </span>
                        <span class="api-div-container-bottom-item">
                            <span class="api-div-container-bottom-item-left"><label style="margin-right: 10px;">接口描述</label></span>
                            <span class="api-div-container-bottom-item-right"><label style="margin-left: 10px;">{{apiObj.desc}}</label></span>
                        </span>
                    </div>
                    <!--接口配置 -> 条件配置-->
                    <div v-if="page_api_index == 2" class="api-div-container-bottom">

                    </div>
                    <!--接口配置 -> 结果配置-->
                    <div v-if="page_api_index == 3" class="api-div-container-bottom">

                    </div>
                    <!--接口配置 -> 权限配置-->
                    <div v-if="page_api_index == 4" class="api-div-container-bottom">

                    </div>
                    <!--模板配置 -> 权限配置-->
                    <div v-if="page_api_index == 5" class="api-div-container-bottom" style="background-color: #303030;overflow:auto;">
                        <el-input class="tempalte-area" type="textarea" v-model="tempalteStr" :rows="40" resize="none"></el-input>
                    </div>
                    <div class="api-div-container-bottom-1">
                        <el-button type="primary" style="float:right;margin-right: 20px;" @click="updateTemplate">确 定</el-button>
                        <el-button type="primary" style="float:right;margin-right: 20px;" @click="resetApiObj">重 置</el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 新建数据接口弹出框 -->
    <el-dialog title="新建接口" v-model="addApiVisible" width="40%">
        <el-form :model="newApiObj" :rules="addApiRules" ref="addApiForm" label-width="100px">
            <el-form-item label="接口名称" prop="name">
                <el-input v-model="newApiObj.name"></el-input>
            </el-form-item>
            <el-form-item label="操作类型" prop="type">
                <el-select v-model="newApiObj.type" placeholder="请选择操作类型">
                    <el-option label="新增" value="put"></el-option>
                    <el-option label="查询" value="get"></el-option>
                    <el-option label="修改" value="post"></el-option>
                    <el-option label="删除" value="delete"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="接口描述" prop="desc">
                <el-input type="textarea" v-model="newApiObj.desc" resize="none" :rows="4"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addVisible = false">取 消</el-button>
                    <el-button type="primary"  @click="saveAddApi('addApiForm')">确 定</el-button>
                </span>
        </template>
    </el-dialog>
</template>

<script>
    import { ElMessage, ElMessageBox } from "element-plus";
    import {transform} from "../../utils/utils";
    import { getApis, create, update} from "../../api/api";
    import { formatJsonToStr} from "../../utils/utils";
    import { getDefaultTemplate, inItDataTemplate, checkTemplate} from "../../components/template/apiTempalte";

    export default {
        data() {
            return {
                api_count: 0,
                searchText: '',
                homeShow: 0,
                tables: [],
                addVisible: false,
                addApiVisible: false,
                newApiObj:{},
                apiObj:{},
                apiObjCopy:{},
                page_index: 0, //0表示table信息编辑 1表示api信息编辑
                page_api_index: 1, //编辑api页面的子菜单页面标记
                page_table_index: 1,
                navigateBar:{
                    table: "",
                    api: ""
                },
                tempalteStr: "",
                addRules:{
                    tableName: [
                        { required: true, message: '请输入数据对象名称', trigger: 'blur' },
                        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
                        {
                            validator: function(rule, value, callback) {
                                if (/[a-zA-z]$/.test(value) == false) {
                                    callback(new Error("请输入英文"));
                                } else {
                                    //校验通过
                                    callback();
                                }
                            },
                            trigger: "blur"
                        }
                    ],
                    tableAlias: [
                        { required: true, message: '请输入数据对象标识', trigger: 'blur' },
                        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
                        {
                            validator: function(rule, value, callback) {
                                if (/^[\u4e00-\u9fa5]+$/.test(value) == false) {
                                    callback(new Error("请输入中文"));
                                } else {
                                    //校验通过
                                    callback();
                                }
                            },
                            trigger: "blur"
                        }
                    ],
                },
                addApiRules:{
                    name: [
                        { required: true, message: '请输入接口名称', trigger: 'blur' },
                        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
                    ],
                    type: [
                        { required: true, message: '请选择操作类型', trigger: 'change' }
                    ],
                }
            }
        },
        created(){
            this.onLoad();
        },
        watch: {

        },
        computed:{

        },
        mounted(){

        },
        methods: {
            //初始化方法
            onLoad() {
                this.getAllTables();
            },
            //查询所有集合
            getAllTables(){
                getApis({"createUser": "100000"}).then((res) => {
                    this.tables = res.data;
                });
            },
            //打开新建API窗口
            openAddApi(tableName){
                this.newApiObj.tableName = tableName;
                this.addApiVisible = true;
            },
            //打开api编辑界面
            openApiEditPage(tableName, api){
                this.navigateBar = {
                    table: tableName,
                    api: api.name
                }
                this.apiObj = api
                this.apiObjCopy = api;
                this.tempalteStr = formatJsonToStr(api);
                this.page_index = 2;

            },
            //重置api对象
            resetApiObj(){
                this.apiObj = this.apiObjCopy
                this.tempalteStr = formatJsonToStr(this.apiObj);
            },
            //提交模板编辑(模板是最终提交的数据，图形化操作结果会更新模板数据)
            updateTemplate(){
                this.updateApi(this.tempalteStr);
            },
            updateApi(api){
                //校验
                let data = checkTemplate(api);
                if(data != null){
                    console.log(data)
                    update(data).then((res) => {
                        if(res){
                            this.$message.success("更新成功");
                            this.getAllTables();
                            return true;
                        }else{
                            this.$message.error("更新失败");
                            return false;
                        }
                    });
                }else{
                    this.$message({
                        message: '模板格式有误，请重新填写！',
                        type: 'error'
                    });
                    return false;
                }
            },
            //提交新建api接口
            saveAddApi(formName){
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        //生成API数据模板
                        let template = inItDataTemplate(this.newApiObj);
                        this.submitApi(template);
                        this.newApiObj = {};
                        this.addApiVisible = false;
                    }
                });
            },
            submitApi(api){
                //校验
                let data = checkTemplate(api);
                if(data != null){
                    create(data).then((res) => {
                        if(res){
                            this.$message.success("创建成功");
                            this.getAllTables();
                            return true;
                        }else{
                            this.$message.error("创建失败");
                            return false;
                        }
                    });
                }else{
                    this.$message({
                        message: '模板格式有误，请重新填写！',
                        type: 'error'
                    });
                    return false;
                }
            }
        }
    };
</script>

<style scoped>
    .bg{
        width: 100%;
        height: 100%;
    }

    .page-left{
        float: left;
        width: 25%;
        height: 100%;
    }

    .page-left-top{
        float: left;
        width: 100%;
        height: 10%;
        background-color: #242f42;
        display: flex;
        flex-direction: row;
        justify-content: space-around;
    }

    .page-left-top-search{
        margin-top: 20px;
        float: left;
        width: 90%;
        height: 30px;
        border: 0px solid #6974B3;
        border-radius: 5px;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
    }

    .page-left-top-search >>> input{
        float: left;
        height: 30px;
    }

    .page-left-top-create{
        margin-top: 20px;
        float: left;
        width: 22%;
        height: 30px;
        background-color: #2D8CF0;
        border-radius: 5px;
        text-align: center;
        font-size: 14px;
        color: #ffffff;
        line-height: 28px;
        letter-spacing: 1px;
        cursor: pointer;
    }

    .page-left-bottom{
        float: left;
        width: 100%;
        height: 90%;
        overflow-y: scroll;
        overflow-x: hidden;
    }

    .page-right{
        float: left;
        margin-left: 1%;
        width: 74%;
        height: 100%;
    }

    .index-div{
        width: 100%;
        height: 100%;
    }

    .index-div-top{
        float: left;
        width: 100%;
        height: 100px;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
    }

    .index-div-top-item{
        width: 24%;
        height: 100px;
        background-color: #ffffff;
        border-radius: 5px;
    }

    .table-div{
        width: 100%;
        height: 100%;
    }

    .api-div{
        width: 100%;
        height: 100%;
        background-color: #ffffff;
    }

    .api-div-container, .table-div-container{
        float: left;
        margin-top: 0px;
        width:100%;
        height: 100%;
    }


    .api-div-container-top, .table-div-container-top{
        float: left;
        width: 80%;
        height: 60px;
    }

    .api-div-container-bottom-1{
        float: left;
        margin-top: 30px;
        width:100%;
        height: 50px;
    }

    .api-div-container-top-right, .table-div-container-top-right{
        float: right;
        width: 20%;
        height: 60px;
        background-color: #242f42;
    }

    .api-div-container-top-right-home, .table-div-container-top-right-home{
        float: right;
        margin-top: 12.5px;
        margin-right: 15px;
        width:35px;
        height: 35px;
        cursor: pointer;
    }

    .table-div-container-navigate, .api-div-container-navigate{
        float: left;
        margin-left: 20px;
        margin-top: 10px;
        width: 60%;
        height: 20px;
        font-size: 14px;
        letter-spacing: 1px;
    }

    .api-div-container-bottom, .table-div-container-bottom{
        float: left;
        margin-top: 10px;
        width: 100%;
        height: 400px;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

    .api-div-container-bottom-item{
        margin-top: 10px;
        width: 100%;
        height: 40px;
        display: flex;
        flex-direction: row;
        justify-content: space-around;
    }

    .api-div-container-bottom-item-left{
        width: 25%;
        height: 38px;
        border: 2px solid #EFEFEF;
        text-align: right;
        color:#333333;
        font-size: 15px;
        line-height: 38px;
        font-weight: bold;
        overflow: hidden;
    }

    .api-div-container-bottom-item-right{
        width: 71%;
        height: 38px;
        border: 1px solid #EFEFEF;
        text-align: left;
        overflow: hidden;
        font-size: 15px;
        line-height: 38px;
    }

    .item-content{
        float: left;
        width:90%;
        height: 100%;
    }

    .item-content-logo{
        float: left;
        margin-top: 19px;
        width: 20px;
        height: 20px;
    }

    .item-content-title{
        float: left;
        margin-left: 10px;
        width: 60%;
        height: 100%;
        white-space:nowrap;
        text-overflow:ellipsis;
        overflow: hidden;
    }

    .item-content-btn{
        float: right;
        margin-top: 18px;
        width: 20px;
        height: 20px;
    }

    /*滚动条样式*/
    ::-webkit-scrollbar {
        width: 14px;
        height: 14px;
        background-color: #242f42;
    }

    ::-webkit-scrollbar-track,
    ::-webkit-scrollbar-thumb {
        border-radius: 999px;
        border: 5px solid transparent;
    }
    /*滚动柱子的凹槽*/
    ::-webkit-scrollbar-track {
        box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.2) inset;
    }
    /*滚动柱子*/
    ::-webkit-scrollbar-thumb {
        min-height: 20px;
        background-clip: content-box;
        box-shadow: 0 0 0 5px rgba(0, 0, 0, 0.2) inset;
    }

    ::-webkit-scrollbar-corner {
        background: transparent;
    }

    .tempalte-area /deep/ .el-textarea__inner{
        color: #FFFFFF;
        background-color: #242f42;
        font-weight: bold;
        font-size: 15px;
    }
</style>
