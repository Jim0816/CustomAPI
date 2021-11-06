<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-cascades"></i> 模型列表
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-input v-model="query.name" placeholder="请输入表名" class="handle-input mr10"></el-input>
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
                <el-button type="primary" style="float: right;" @click="openAdd(0)">手动创建</el-button>
                <el-button type="primary"  style="float: right;" @click="openAdd(1)">模板创建</el-button>
            </div>
            <el-table :data="tableData" border class="table" ref="multipleTable" header-cell-class-name="table-header">
                <el-table-column type="index" label="序号" align="center" width="80"></el-table-column>
                <el-table-column prop="tableName" label="表名" align="center"></el-table-column>
                <el-table-column  label="字段数量" align="center" width="50">
                    <template #default="scope">
                        {{scope.row.fields == undefined ? 0 : scope.row.fields.length}}
                    </template>
                </el-table-column>
                <el-table-column prop="permission" label="权限" align="center">
                    <template #default="scope">
                        {{JSON.stringify(scope.row.permission)}}
                    </template>
                </el-table-column>
                <el-table-column prop="time" label="创建时间" align="center"></el-table-column>
                <el-table-column prop="desc" label="备注" align="center"></el-table-column>
                <el-table-column label="操作" width="200" align="center">
                    <template #default="scope">
                        <el-button type="text" icon="el-icon-edit" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                        <el-button type="text" icon="el-icon-view" @click="viewModel(scope.$index, scope.row)">查看</el-button>
                        <el-button type="text" icon="el-icon-delete" class="red" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination background layout="total, prev, pager, next" :current-page="query.pageIndex"
                               :page-size="query.pageSize" :total="pageTotal" @current-change="handlePageChange"></el-pagination>
            </div>
        </div>

        <!-- 手动新增模型弹出框 -->
        <el-dialog  title="新增模型" v-model="addVisible" width="60%"  append-to-body>
            <div class="add-div">
                <div class="top">
                    <div style="width:100%;height: 50px;"></div>
                    <div style="width:100%;height:28px;">
                        <span id="top-menu-0" class="menu-item"  @click="switchMenuPage(0)">基本</span>
                        <span id="top-menu-1" class="menu-item"  @click="switchMenuPage(1)">字段</span>
                        <span id="top-menu-2" class="menu-item"  @click="switchMenuPage(2)">权限</span>
                    </div>
                </div>
                <div class="center">
                    <div v-if="center_page!=0" class="center_tools">
                        <span class="tool_span" style="margin-left: 10px;" @click="addFieldVisible=true">
                            <span style="float:left;width: 20px;height: 20px;"><img style="width:100%;height:100%;" src="../../assets/img/add_field.jpg"/></span>
                            <span style="float:left;margin-left:2px;color:#ffffff;">添加字段</span>
                        </span>
                        <span class="tool_span" style="margin-left: 10px;">
                            <span style="float:left;width: 20px;height: 20px;"><img style="width:100%;height:100%;" src="../../assets/img/del_field.jpg"/></span>
                            <span style="float:left;margin-left:2px;color:#ffffff;">删除字段</span>
                        </span>
                    </div>

                    <div v-if="center_page == 0" class="center_0">
                        <span style="float: left;margin-top:50px;margin-left:0px;width:500px;height: 300px;">
                            <el-form :model="tableObj" ref="ruleForm" label-width="80px" style="margin-left: 30px;">
                                <el-form-item required label="表名" prop="tableName">
                                    <el-input v-model="tableObj.tableName" @change="checkFieldName" style="background-color: red;"></el-input>
                                </el-form-item>
                                <el-form-item required label="存储类型" prop="dbType">
                                    <el-radio-group v-model="tableObj.dbType" :disabled="true">
                                      <el-radio-button label="MongoDB">MongoDB</el-radio-button>
                                      <el-radio-button label="Mysql">&nbsp&nbspMysql&nbsp&nbsp</el-radio-button>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item required label="数据描述" prop="desc">
                                    <el-input type="textarea" resize="none" :rows="5" v-model="tableObj.desc"></el-input>
                                </el-form-item>
                            </el-form>
                        </span>
                    </div>
                    <div v-if="center_page == 1" class="center_1">
                        <el-dialog
                                width="30%"
                                title="添加字段"
                                v-model="addFieldVisible"
                                append-to-body>
                            <div class="add-field-div">
                                <el-form :model="fieldForm" :rules="fieldFormRule" ref="fieldForm" label-width="100px" class="demo-ruleForm">
                                    <el-form-item label="字段名称" prop="name">
                                        <el-input v-model="fieldForm.name"></el-input>
                                    </el-form-item>
                                    <el-form-item label="字段类型" prop="type">
                                        <el-select v-model="fieldForm.type" placeholder="请选择字段数据类型" @change="changeType" clearable style="width: 100%;">
                                            <el-option label="String" value="String"></el-option>
                                            <el-option label="Int" value="Int"></el-option>
                                            <el-option label="Date" value="Date"></el-option>
                                            <el-option label="Float" value="Float"></el-option>
                                            <el-option label="Double" value="Double"></el-option>
                                            <el-option label="Array" value="Array"></el-option>
                                            <el-option label="Object" value="Object"></el-option>
                                        </el-select>
                                    </el-form-item>
                                    <el-form-item label="字段必填" prop="isRequire">
                                        <el-radio v-model="fieldForm.isRequire" label="1">是</el-radio>
                                        <el-radio v-model="fieldForm.isRequire" label="0">否</el-radio>
                                    </el-form-item>
                                    <el-form-item label="字段唯一" prop="isUnique">
                                        <el-radio v-model="fieldForm.isUnique" label="1">是</el-radio>
                                        <el-radio v-model="fieldForm.isUnique" label="0">否</el-radio>
                                    </el-form-item>
                                    <el-form-item label="字段长度" prop="length">
                                        <el-input-number v-model="fieldForm.length" :disabled="isEdit" :min="-1" :max="300" label="描述文字"></el-input-number>
                                    </el-form-item>
                                    <el-form-item label="默认值" prop="default">
                                        <el-input v-model="fieldForm.default"></el-input>
                                    </el-form-item>
                                    <el-form-item label="备注" prop="remark">
                                        <el-input type="textarea" resize="none" :rows="5" v-model="fieldForm.remark"></el-input>
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" @click="submitAddField('fieldForm')">立即创建</el-button>
                                        <el-button @click="resetAddField('fieldForm')">重置</el-button>
                                    </el-form-item>
                                </el-form>
                            </div>
                        </el-dialog>
                        <el-table :data="tableObj.fields" border class="table" ref="multipleTable"  size="mini"
                                  :header-cell-style="{background:'#eef1f6',color:'#303030'}">
                            <el-table-column type="selection" width="55"></el-table-column>
                            <el-table-column prop="name" label="字段名称" align="center"></el-table-column>
                            <el-table-column prop="type" label="类型" align="center"></el-table-column>
                            <el-table-column prop="isRequire" label="必填" align="center"></el-table-column>
                            <el-table-column prop="isUnique" label="唯一" align="center"></el-table-column>
                            <el-table-column prop="length" label="长度" align="center"></el-table-column>
                            <el-table-column prop="default" label="默认值" align="center"></el-table-column>
                            <el-table-column prop="remark" label="备注" align="center"></el-table-column>
                            <el-table-column label="操作" width="180" align="center">
                                <template #default="scope">
                                    <el-button type="text" icon="el-icon-edit" @click="handleEditField(scope.$index, scope.row)">编辑
                                    </el-button>
                                    <el-button type="text" icon="el-icon-delete" class="red"
                                               @click="handleDeleteField(scope.$index)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </div>
                    <div v-if="center_page == 2" class="center_2">权限</div>
                    <div class="center-tips">
                        <p>  字段总数：{{tableObj.fields == undefined ? 0 : tableObj.fields.length}}</p>
                    </div>
                </div>
                <div class="bottom">
                    <span style="float:left;width:100%;height:20px;">
                        <el-button type="primary"  style="float:right;" @click="saveTableObj(0)">保&nbsp&nbsp存</el-button>
                    </span>
                </div>
            </div>
        </el-dialog>

        <!-- 基于模板新增模型弹出框 -->
        <el-dialog  title="新增模型" v-model="addVisible1" width="60%"  append-to-body>
            <div class="add-div">
                <div class="top" style="background-color: #303030;height: 420px;overflow: auto;">
                    <el-input class="tempalte-area" type="textarea" v-model="tempalteStr" :rows="100" resize="none" ></el-input>
                </div>
                <div class="bottom">
                    <span style="float:left;width:100%;height:20px;">
                        <el-button type="primary"  style="float:right;" @click="saveTableObj(1)">保&nbsp&nbsp存</el-button>
                    </span>
                </div>
            </div>
        </el-dialog>

        <!-- 查看数据对象信息弹出框 -->
        <el-dialog  title="查看模型" v-model="viewVisible" width="40%"  append-to-body>
            <div class="view-div">
                <el-input class="tempalte-area" type="textarea" v-model="tempalteStr" :rows="100" resize="none" :disabled="true"></el-input>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import { ElMessage, ElMessageBox } from "element-plus";
    import { allTables, createTable, dropTable, getTables} from "../../api/baseService";
    import { formatJsonToStr} from "../../utils/utils";


    export default {
        data() {
            return {
                query: {
                    params: "",
                    pageIndex: 1,
                    pageSize: 10,
                },
                tableData: [],
                tableObj:{
                    tableName: "",
                    dbType: "MongoDB",
                    desc: "",
                    fields:[]
                },
                fieldForm:{
                    name: "",
                    type: "",
                    isRequire: '0',
                    isUnique: '0',
                    length: 300,
                    default: "",
                    remark: ""
                },
                isEdit: true, //是否允许编辑长度字段
                center_page: 0,
                pageTotal: 0,
                addVisible: false,
                addVisible1: false,
                viewVisible: false,
                addFieldVisible: false,
                form: {},
                tempalteStr: "",
                fileList: [],
                fieldFormRule: {
                    name: [
                        { required: true, message: '请输入字段名称', trigger: 'blur' },
                        { min: 3, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
                    ],
                    type: [
                        { required: true, message: '请选择字段数据类型', trigger: 'change' }
                    ],
                    isRequire: [
                        { required: true, message: '请选择是否必填', trigger: 'change' }
                    ],
                    isUnique: [
                        { required: true, message: '请选择是否唯一', trigger: 'change' }
                    ],
                },
            }
        },
        created(){
            this.onLoad();
        },
        watch: {

        },
        computed:{

        },
        methods: {
            //初始化方法
            onLoad(){
                this.getData();
            },
            // 获取表格数据
            getData(){
                //查询所有集合
                allTables().then((res) => {
                    this.tableData = res;
                });
            },
            openAdd(type){
                //手动创建
                if(type==0){
                    this.addVisible = true;
                    this.center_page = 0;
                    this.switchMenuPage(0);
                }else{
                    //模板创建
                    this.addVisible1 = true
                    //获取模板
                    getTables({"tableName": "sys_template"}).then((res) => {
                        if(res.length > 0){
                            this.tempalteStr = formatJsonToStr(res[0]);
                        }
                    });
                }

            },
            //提交添加字段
            submitAddField(formName){
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        //将fieldForm中的isRequire、isUnique转换为整型，后端接受整型数据
                        this.fieldForm.isRequire = parseInt(this.fieldForm.isRequire);
                        this.fieldForm.isUnique = parseInt(this.fieldForm.isUnique);
                        this.tableObj.fields.push(this.fieldForm);
                        this.addFieldVisible = false;
                        this.fieldForm = {name: "", type: "", isRequire: '0', isUnique: '0', length: 300, default: "", remark: ""}
                    } else {
                        return false;
                    }
                });
            },
            changeType(){
                let fieldType = this.fieldForm.type;
                //String和Int类型允许指定长度
                if(fieldType == "Int" || fieldType == "String"){
                    this.isEdit = false;
                    this.fieldForm.length = 300
                }else{
                    this.isEdit = true;
                    this.fieldForm.length = -1
                }
            },
            //提交重置字段页面
            resetAddField(formName){
                this.$refs[formName].resetFields();
            },
            //规范用户创建的表名的命名（不是系统表sys）
            formatCustomTableName(originName){
                if(originName.indexOf("tb_") == -1){
                    return "tb_" + originName;
                }
                return originName;
            },
            //校验数据库名称是否合法
            checkFieldName(){
                let formatTableName = this.formatCustomTableName(this.tableObj.tableName);
                console.log(formatTableName)
                getTables({tableName: formatTableName}).then((res) => {
                        console.log(res)
                        if(res){
                            //校验成功
                        }else{
                            //校验失败
                        }
                    });
            },
            //提交前，校验数据
            checkDataBeforeSubmit(data){
                if(data.tableName != '' && data.fields != undefined && data.fields.length > 0 && data.dataType != ''){
                    //继续校验字段列表
                    for(let i=0 ; i<data.fields.length ; i++){
                        let fieldObj = data.fields[i];
                        //名称校验
                        if(fieldObj.name == undefined || fieldObj.name == '' ){
                            console.log("字段名称校验失败!")
                            return false;
                        }
                        //长度校验
                        if(fieldObj.length == undefined || fieldObj.length == '' || fieldObj.length > 300 || fieldObj.length < -1){
                            console.log("字段长度校验失败!")
                            return false;
                        }
                        //类型校验
                        if(fieldObj.type == undefined || fieldObj.type == '' ){
                            console.log("字段类型校验失败!")
                            switch (fieldObj.type) {
                                case 'String':
                                    if(fieldObj.length < 0 || fieldObj.length > 300)
                                        return false;
                                    break;
                                case 'Int':
                                    if(fieldObj.length < 0 || fieldObj.length > 300)
                                        return false;
                                    break;
                                default:
                                    if(fieldObj.length != -1)
                                        return false;
                                    break;
                            }
                            return false;
                        }
                        //必填项校验
                        if(fieldObj.isRequire == undefined || (fieldObj.isRequire != 0 && fieldObj.isRequire != 1)){
                            console.log("字段必填项校验失败!")
                            return false;
                        }
                        //唯一项校验
                        if(fieldObj.isUnique == undefined || (fieldObj.isUnique != 0 && fieldObj.isUnique != 1)){
                            console.log("字段唯一项校验失败!")
                            return false;
                        }
                    }
                }
                return true;
            },
            //创建数据对象
            saveTableObj(type){
                if(type == 1){
                    //模板添加，需要校验模板
                    try{
                        this.tableObj = JSON.parse(this.tempalteStr);
                    }catch (e) {
                        this.$message({
                            message: '模板格式有误，请重新填写！',
                            type: 'error'
                        });
                        return;
                    }

                }
                let data = this.tableObj;
                //规范表名
                data.tableName = this.formatCustomTableName(data.tableName);
                //校验数据格式(预留)
                console.log(data)
                let checkResult = this.checkDataBeforeSubmit(data);
                if(checkResult){
                    //提交数据
                    /*createTable(data).then((res) => {
                        console.log(res)
                        if(res){
                            this.addVisible = false;
                            this.addVisible1 = false;
                            this.$message({
                                message: '创建成功',
                                type: 'success'
                            });
                            this.getData();
                        }
                    });*/
                }else{
                    this.$message({
                        message: '提交数据校验失败,请完善数据对象信息!',
                        type: 'warning'
                    });
                }
                //刷新填写的历史记录
                this.tableObj = {tableName: "", dbType: "MongoDB", desc: "", fields:[]}
            },
            //删除表对象
            removeTable(tableName){
                if(tableName.indexOf("sys_") != -1){
                    this.$message({
                        message: '系统数据对象不允许删除',
                        type: 'warning'
                    });
                }else{
                    let data = {"tableName": tableName};
                    dropTable(data).then((res) => {
                        this.getData();
                    });
                }
            },
            switchMenuPage(page){
                let e0 = document.getElementById("top-menu-0");
                let e1 = document.getElementById("top-menu-1");
                let e2 = document.getElementById("top-menu-2");
                console.log(e0)
                this.center_page = page;
                /*switch (page) {
                    case 0:
                        e0.style.backgroundColor = '#303030'
                        e0.style.color = '#ffffff'
                        e1.style.backgroundColor = '#404040'
                        e1.style.color = '#B0B0B0'
                        e2.style.backgroundColor = '#404040'
                        e2.style.color = '#B0B0B0'
                        break;
                    case 1:
                        e1.style.backgroundColor = '#303030'
                        e1.style.color = '#ffffff'
                        e0.style.backgroundColor = '#404040'
                        e0.style.color = '#B0B0B0'
                        e2.style.backgroundColor = '#404040'
                        e2.style.color = '#B0B0B0'
                        break;
                    case 2:
                        e2.style.backgroundColor = '#303030'
                        e2.style.color = '#ffffff'
                        e0.style.backgroundColor = '#404040'
                        e0.style.color = '#B0B0B0'
                        e1.style.backgroundColor = '#404040'
                        e1.style.color = '#B0B0B0'
                        break;
                }*/
            },
            // 查询操作
            handleSearch() {
                query.pageIndex = 1;
            },
            // 分页导航
            handlePageChange(val){
                query.pageIndex = val;
                getData();
            },
            // 删除操作
            handleDeleteField(index){
                this.tableObj.fields.splice(index, 1);
            },
            handleDelete(index,row){
                // 二次确认删除
                ElMessageBox.confirm("确定要删除吗？", "提示", {
                    type: "warning",
                }).then(() => {
                    this.removeTable(row.tableName);
                }).catch(() => {});
            },
            handleEditField(index, row){
                this.fieldForm = row;
                this.addFieldVisible = true;
            },
            handleEdit (index, row){
                console.log(row)
                this.form = row;
                if(this.fileList.length == 0){
                    this.fileList.push({
                        name: row.name,
                        url: row.photo
                    });
                }
                this.editVisible = true;
            },
            viewModel(index, row){
                this.tempalteStr = formatJsonToStr(row);
                this.viewVisible = true;
            },
            handleRemove(file, fileList) {
                this.fileList = [];
            },
            handleChange(file, fileList) {
                console.log(file,fileList)
            },
            handExceed(file, fileList){
                this.$message({
                    message: '只允许上传一张图片',
                    type: 'warning'
                });
            }
        }
    };
</script>

<style lang="less" scoped>

    .add-div{
        margin-top: -30px;
        width: 100%;
        height: 500px;
        overflow: hidden;
        background-color: #FFFFFF;
    }

    .view-div{
        margin-top: -30px;
        width: 100%;
        height: 600px;
        overflow: hidden;
        background-color: #303030;
        overflow: auto;
    }

    .top{
        float: left;
        width: 100%;
        height: 30px;
        background-color: #404040;
        display: flex;
        flex-direction: column;
        align-content: flex-start;
    }

    .center{
         float: left;
         width: 100%;
         margin-top: 0px;
         height: 400px;
         background-color: #FFFFFF;
     }

    .center_tools{
        float: left;
        background-color: #303030;
        width: 100%;
        height: 29px;
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        border-bottom: 1px solid #404040;
    }

    .tool_span{
        margin-top: 5px;
        height: 20px;
        cursor: pointer;
    }

    .center_0{
        float: left;
        background-color: #303030;
        width: 100%;
        height: 370px;
        overflow: auto;
    }

    .center_1{
        float: left;
        background-color: #303030;
        width: 100%;
        height: 370px;
        overflow: auto;
    }

    .center-tips{
        float: left;
        margin-top: 0px;
        background-color: #404040;
        width: 100%;
        height: 20px;
        color: #ffffff;
        font-size: 13px;
        letter-spacing: 1px;
    }

    .add-field-div{
        margin-top: 0px;
        width: 100%;
        height: 600px;
        overflow: hidden;
    }

    .center_1 /deep/ .el-table--fit{
        padding: 0px;
    }
    .center_1 /deep/  .el-table, .el-table__expanded-cell {
        background-color: transparent;
    }

    .center_1 /deep/ .el-table tr {
        color: #ffffff!important;
        background-color: transparent!important;
    }
    .center_1 /deep/  .el-table--enable-row-transition .el-table__body td, .el-table .cell{
        background-color: transparent;
    }





    .bottom{
        float: left;
        width: 100%;
        margin-top: 20px;
        height: 40px;
    }


    .menu-item{
        float: left;
        width: 80px;
        height: 100%;
        border: 1px solid #505050;
        color: #ffffff;
        text-align: center;
        line-height: 30px;
        letter-spacing: 1px;
        cursor: pointer;
    }

    .tempalte-area /deep/ .el-textarea__inner{
        color: #FFFFFF;
        background-color: #303030;
        font-weight: bold;
        font-size: 15px;
    }

    .handle-box {
        margin-bottom: 20px;
    }

    .handle-input {
        width: 300px;
        display: inline-block;
    }
    .table {
        width: 100%;
        font-size: 14px;
    }
    .red {
        color: #ff0000;
    }
    .mr10 {
        margin-right: 10px;
    }



</style>
