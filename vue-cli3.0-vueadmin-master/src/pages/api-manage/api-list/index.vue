<template>
    <div class="page-container">
        <div class="page-container-top"></div>
        <div class="page-container-center">
            <vxe-grid
                border
                resizable
                keep-source
                ref="xGrid"
                id="toolbar_demo_1"
                height="500"
                :print-config="{}"
                :import-config="{}"
                :export-config="{}"
                :columns="tableColumn"
                :seq-config="{startIndex: (tablePage.currentPage - 1) * tablePage.pageSize}"
                :toolbar-config="tableToolbar"
                :data="tableData"
                :custom-config="{storage: true}"
                :edit-config="{trigger: 'click', mode: 'row', showStatus: true}"
                @toolbar-button-click="toolbarButtonClickEvent"
                @toolbar-tool-click="toolbarToolClickEvent">
                <!--操作行-->
                <template #operate="{ row }">
                    <vxe-button icon="fa fa-edit" title="编辑" circle @click="editAPI(row)"></vxe-button>
                    <vxe-button icon="fa fa-trash" title="删除" circle @click="removeAPI(row)"></vxe-button>
                    <vxe-button icon="fa fa-eye" title="查看" circle @click="showAPI(row)"></vxe-button>
                    <vxe-button icon="fa fa-copy" title="复制接口地址" circle v-clipboard:copy="row.url" v-clipboard:success="onCopy" v-clipboard:error="onError"></vxe-button>
                </template>
                <!--分页-->
                <template #pager>
                    <vxe-pager
                        :layouts="['Sizes', 'PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'FullJump', 'Total']"
                        :current-page.sync="tablePage.currentPage"
                        :page-size.sync="tablePage.pageSize"
                        :total="tablePage.total"
                        @page-change="handlePageChange">
                    </vxe-pager>
                </template>
            </vxe-grid>

            <!--添加数据表弹出框-->
            <vxe-modal v-model="addAPIModal" title="编辑数据对象" width="1000" height="550" show-zoom resize transfer >
                <template #default>
                    <div class="addAPI-container">
                        <el-form :model="apiObj" :rules="apiRules" ref="apiForm" label-width="100px" class="demo-ruleForm">
                            <el-form-item label="接口名称" prop="name">
                                <el-input v-model="apiObj.name"></el-input>
                            </el-form-item>
                            <el-form-item label="操作对象" prop="tableName" style="margin-top: 40px;">
                                <el-select v-model="apiObj.tableName" placeholder="请选择操作的数据对象">
                                    <el-option v-for="item in dic.tableDic" :key="item.tableName" :label="item.tableName" :value="item.tableName"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="操作类型" prop="operateTypeCopy" style="margin-top: 40px;">
                                <el-select v-model="apiObj.operateTypeCopy" placeholder="请选择操作类型" @change="changeOpeType">
                                    <el-option v-for="item in dic.operateDic" :key="item.value" :label="item.label" :value="item.value"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="授权用户" prop="permission" style="margin-top: 40px;">
                                <el-select v-model="apiObj.permission" placeholder="请选择接口的授权用户" multiple collapse-tags>
                                    <el-option v-for="item in dic.userDic" :key="item.id" :label="item.nickname" :value="item.id"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="接口要求" prop="require" style="margin-top: 40px;">
                                <el-input type="textarea" v-model="apiObj.require"></el-input>
                            </el-form-item>
                            <el-form-item label="接口描述" prop="desc" style="margin-top: 40px;">
                                <el-input type="textarea" v-model="apiObj.desc"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="submitForm('apiForm')">立即创建</el-button>
                                <el-button @click="resetForm('apiForm')">重置</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </template>
            </vxe-modal>

            <!--查看数据对象信息弹框-->
            <vxe-modal v-model="showAPIModal" title="查看接口信息" width="600" height="420" show-zoom resize remember>
                <template #default>
                    <div class="show-table-info-div">
                        <div class="show-table-info-basic">
                            <vxe-form :data="showAPIObj" title-align="right" title-width="80">
                                <!--基本信息-->
                                <vxe-form-item title="基本信息" span="24" title-align="left" title-width="200px" :title-prefix="{icon: 'el-icon-s-order'}"></vxe-form-item>
                                <vxe-form-item title="接口名称" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showAPIObj.name" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                                <vxe-form-item title="操作对象" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showAPIObj.tableName" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                            </vxe-form>
                        </div>

                        <!--操作要求-->
                        <div class="show-table-info-fields">
                            <vxe-form :data="showAPIObj" title-align="right" title-width="80">
                                <vxe-form-item title="操作要求" span="24" title-align="left" title-width="200px" :title-prefix="{icon: 'el-icon-s-order'}"></vxe-form-item>
                                <vxe-form-item title="操作类型" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showAPIObj.require.operate" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                                <vxe-form-item title="操作规则" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-textarea  v-model="showAPIObjCondition" :autosize="{minRows: 5, maxRows: 8}" readonly></vxe-textarea>
                                    </template>
                                </vxe-form-item>
                            </vxe-form>
                        </div>

                        <!--使用说明-->
                        <div class="show-table-info-fields">
                            <vxe-form :data="showAPIObj" title-align="right" title-width="80">
                                <vxe-form-item title="接口使用" span="24" title-align="left" title-width="200px" :title-prefix="{icon: 'el-icon-s-order'}"></vxe-form-item>
                                <vxe-form-item title="接口地址" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showAPIObj.url" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                                <vxe-form-item title="接口描述" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-textarea v-model="showAPIObj.desc"  :autosize="{minRows: 3, maxRows: 6}" readonly></vxe-textarea>
                                    </template>
                                </vxe-form-item>
                                <vxe-form-item title="授权用户" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showAPIObj.permission" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                            </vxe-form>
                        </div>
                    </div>
                </template>
            </vxe-modal>

        </div>
        <div class="page-container-bottom"></div>
    </div>
</template>

<script>
    import { get, list, add, update, remove, templates } from '@/api/interface'
    import { list as getTableList} from '@/api/table'
    import { list as getUserList } from '@/api/user'
    import { formatJsonToStr, checkTemplate } from '@/utils/stringUtil'
    export default {
        data() {
            return {
                tableData: [],
                tablePage: {
                    total: 0,
                    currentPage: 1,
                    pageSize: 10
                },
                tableToolbar: {
                    buttons: [
                        { code: 'addAPI', name: '创建接口', status: 'primary', icon: 'fa fa-plus' },
                    ],
                    refresh: true,
                    import: true,
                    export: true,
                    print: true,
                    zoom: true,
                    custom: true
                },
                tableColumn: [
                    { type: 'seq', width: 60 },
                    { field: 'tableName', title: '数据对象', width: 150, showOverflow: true },
                    { field: 'name', title: '接口名称', showOverflow: true },
                    { field: 'url', title: '接口地址', showOverflow: true },
                    { field: 'permission', title: '授权用户', showOverflow: true },
                    { field: 'require', title: '接口要求', showOverflow: true },
                    { field: 'desc', title: '接口对接说明', showOverflow: true},
                    { title: '操作', width: 200, slots: { default: 'operate' }, align: 'center' }
                ],
                apiDefaultObj: {
                    tableName: '',
                    name: '',
                    desc: '',
                    operateTypeCopy: '',
                    permission: [],
                    require: {}
                },
                apiObj: {},
                showAPIObj: {},
                showAPIObjCondition: '',
                isEdit: 0,
                addAPIModal: false,
                showAPIModal: false,
                apiRules: {
                    name: [
                        { required: true, message: '请输入接口名称', trigger: 'blur' },
                        { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
                    ],
                    tableName: [
                        { required: true, message: '请选择操作的数据对象', trigger: 'change' }
                    ],
                    operateTypeCopy: [
                        { required: true, message: '请选择接口的操作类型', trigger: 'change' }
                    ],
                    permission: [
                        { required: true, message: '请选择接口的授权用户', trigger: 'change' }
                    ],
                    condition: [
                        { required: true, message: '请输入接口操作要求', trigger: 'blur' }
                    ]
                },
                dic:{
                    operateDic:[
                        {label: 'get', value: 'get'},
                        {label: 'add', value: 'add'},
                        {label: 'delete', value: 'delete'},
                        {label: 'update', value: 'update'}
                    ],
                    userDic:[],
                    tableDic: []
                },
                templates:{},
            }
        },
        created () {
            this.init()
        },
        methods: {
            init(){
                this.loadDic()
                this.findList()
            },
            async loadDic(){
                let tableRes = await getTableList()
                let tableList = tableRes.data.filter(item => {
                    return item.tableName.indexOf('sys_') == -1
                })
                this.dic.tableDic = tableList

                let userRes = await getUserList()
                this.dic.userDic = userRes.data
                let templateRes = await templates()
                this.templates = templateRes.data
            },
            findList () {
                this.loading = true
                setTimeout(async () => {
                    this.loading = false
                    this.tablePage.total = 10
                    let res = await list()
                    this.tableData = res.data
                }, 300)
            },
            toolbarButtonClickEvent ({ code }) {
                const $grid = this.$refs.xGrid
                switch (code) {
                    case 'addAPI':
                        // 深拷贝
                        this.apiObj = JSON.parse(JSON.stringify(this.apiDefaultObj))
                        this.apiObj.require = ''
                        this.addAPIModal = true
                        this.isEdit = 0
                        break
                    case 'myBatchDel':
                        // 获取所有被选行
                        const $grid = this.$refs.xGrid
                        const selectRecords = $grid.getCheckboxRecords(true)
                        if(selectRecords.length < 1) {
                            this.$message({
                                message: '请先选择需要删除的数据',
                                type: 'warning'
                            });
                            break
                        }
                        this.$confirm('是否要删除选中数据, 是否继续?', '提示', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(async () => {
                            for(let i = 0; i < selectRecords.length; i++){
                                const res = await remove(selectRecords[i].id)
                                if(!res.data){
                                    this.$message.error('批量删除操作失败')
                                    break;
                                }
                            }
                            this.$message({
                                message: '批量删除成功',
                                type: 'success'
                            });
                            //批量删除500毫秒后刷新列表
                            setTimeout(() => {this.findList()}, 500)
                        })
                        break
                    case 'myExport':
                        $grid.exportData({
                            type: 'csv'
                        })
                        break
                }
            },
            toolbarToolClickEvent ({ code }) {
                const $grid = this.$refs.xGrid
                switch (code) {
                    case 'myPrint':
                        $grid.print()
                        break
                }
            },
            handlePageChange ({ currentPage, pageSize }) {
                this.tablePage.currentPage = currentPage
                this.tablePage.pageSize = pageSize
                this.findList()
            },
            editAPI(row) {
                // 深拷贝
                this.apiObj = JSON.parse(JSON.stringify(row))
                this.apiObj.operateTypeCopy = this.apiObj.require.operate
                this.changeOpeType()
                this.addAPIModal = true
                this.isEdit = 1
            },
            removeAPI(row) {
                this.$confirm('是否要删除此项数据, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(async () => {
                    const res = await remove(row.id)
                    if(res.data) {
                        // 删除成功，刷新列表
                        this.$message({
                            message: '删除成功',
                            type: 'success'
                        });
                        setTimeout(() => {this.findList()}, 200)
                    }
                }).catch(() => {
                    this.$message.error('删除失败')
                })
            },
            showAPI(row) {
                this.showAPIObj = row
                this.showAPIObjCondition = formatJsonToStr(this.showAPIObj.require.condition)
                this.showAPIModal = true
            },
            changeOpeType(){
                let type = this.apiObj.operateTypeCopy
                switch (type) {
                    case 'get':
                        this.apiObj.require = formatJsonToStr(this.templates.get)
                        break
                    case 'add':
                        this.apiObj.require = formatJsonToStr(this.templates.add)
                        break
                    case 'update':
                        this.apiObj.require = formatJsonToStr(this.templates.update)
                        break
                    case 'delete':
                        this.apiObj.require = formatJsonToStr(this.templates.delete)
                        break
                }
            },
            submitForm(formName){
                this.$refs[formName].validate(async (valid) => {
                    if (valid) {
                        if(this.isEdit == 1){
                            //修改
                            let require = checkTemplate(this.apiObj.require)
                            if(require != null){
                                //格式化校验成功
                                this.apiObj.require = require
                                let res = await update(this.apiObj)
                                if(res.result){
                                    this.findList()
                                    this.$message({
                                        message: '修改成功',
                                        type: 'success'
                                    })
                                }else{
                                    this.$message({
                                        message: '修改失败',
                                        type: 'error'
                                    })
                                }
                            }else{
                                console.log("格式校验失败")
                            }
                        }else{
                            //添加
                            let require = checkTemplate(this.apiObj.require)
                            if(require != null){
                                //格式化校验成功
                                this.apiObj.require = require
                                let res = await add(this.apiObj)
                                if(res.result){
                                    this.findList()
                                    this.$message({
                                        message: '创建成功',
                                        type: 'success'
                                    })
                                }else{
                                    this.$message({
                                        message: '创建失败',
                                        type: 'error'
                                    })
                                }
                            }else{
                                console.log("格式校验失败")
                            }
                        }


                    } else {
                        console.log('表单校验失败');
                        return false;
                    }
                })
                this.addAPIModal = false
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            onCopy (e) {
                this.$message.success("接口地址已复制到剪切板！")
            },
            onError (e) {
                this.$message.error("抱歉，复制失败！")
            }
        }
    }
</script>

<style>
    .page-container{
        position: relative;
        width: 100%;
        min-height: 300px;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
    }

    .page-container-top{
        width: 100%;
        height: 0px;
    }

    .page-container-center{
        width: 100%;
    }

    .page-container-bottom{
        width: 100%;
        height: 0px;
    }

    .addAPI-container{
        position: relative;
        width: 100%;
    }

    .require-div{
        width:100%;
        height: 300px;
        border: 1px solid #DCDFE6;
        overflow: auto;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
    }

    .require-div-item{
        width:96%;
        margin-top: 10px;
        margin-left: 2%;
        min-height: 50px;
        border-bottom: 1px solid #DCDFE6;
    }

    .show-table-info-div{
        position: relative;
        width: 100%;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
    }

    .show-table-info-basic{
        width: 100%;
    }

    .show-table-info-fields{
        width: 100%;
    }

    .field-div{
        float: left;
        margin-top: 10px;
        margin-left: 30px;
        width: 80%;
    }

    .show-table-info-other{
        width: 100%;
        height: 50px;
    }


</style>
