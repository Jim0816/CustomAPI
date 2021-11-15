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
                    <vxe-button icon="fa fa-edit" title="编辑" circle @click="editTable(row)"></vxe-button>
                    <vxe-button icon="fa fa-trash" title="删除" circle @click="removeTable(row)"></vxe-button>
                    <vxe-button icon="fa fa-eye" title="查看" circle @click="showTable(row)"></vxe-button>
                    <vxe-button icon="fa fa-copy" title="复制接口地址" circle ></vxe-button>
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
                                    <el-option label="区域一" value="shanghai"></el-option>
                                    <el-option label="区域二" value="beijing"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="操作类型" prop="operate" style="margin-top: 40px;">
                                <el-select v-model="apiObj.require.operate" placeholder="请选择操作类型">
                                    <el-option label="区域一" value="shanghai"></el-option>
                                    <el-option label="区域二" value="beijing"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="授权用户" prop="permission" style="margin-top: 40px;">
                                <el-select v-model="apiObj.permission" placeholder="请选择接口的授权用户">
                                    <el-option label="区域一" value="shanghai"></el-option>
                                    <el-option label="区域二" value="beijing"></el-option>
                                </el-select>
                            </el-form-item>
                            <!--<el-form-item label="操作条件" style="margin-top: 40px;">
                                <div class="require-div">
                                    <div class="require-div-item">
                                        <span style="float:left;width:15%;height: 100%;">
                                            <el-tag type="primary" effect="dark" size="mini" style="margin-left: 15px;">过滤规则</el-tag>
                                        </span>
                                        <span style="float:left;width:85%;height: 100%;">
                                            <el-input v-model="conditionTemplate.filter" placeholder="请输入内容"></el-input>
                                        </span>
                                    </div>
                                    <div class="require-div-item">
                                        <span style="float:left;width:15%;height: 100%;">
                                            <el-tag type="success" effect="dark" size="mini" style="margin-left: 15px;">分页规则</el-tag>
                                        </span>
                                        <span style="float:left;width:85%;height: 100%;">
                                            <el-input v-model="conditionTemplate.limit" placeholder="请输入内容"></el-input>
                                        </span>
                                    </div>
                                    <div class="require-div-item">
                                        <span style="float:left;width:15%;height: 100%;">
                                            <el-tag type="info" effect="dark" size="mini" style="margin-left: 15px;">排序规则</el-tag>
                                        </span>
                                        <span style="float:left;width:85%;height: 100%;">
                                            <el-input v-model="conditionTemplate.sort" placeholder="请输入内容"></el-input>
                                        </span>
                                    </div>
                                </div>
                            </el-form-item>-->
                            <el-form-item label="接口要求" prop="condition" style="margin-top: 40px;">
                                <el-input type="textarea" v-model="apiObj.require.condition"></el-input>
                            </el-form-item>
                            <el-form-item label="接口描述" prop="desc" style="margin-top: 20px;">
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

        </div>
        <div class="page-container-bottom"></div>
    </div>
</template>

<script>
    import { get, list, add, update, remove } from '@/api/interface'
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
                    { field: 'model', title: '数据对象', width: 150, showOverflow: true },
                    { field: 'apiType', title: '操作类型', width: 80, showOverflow: true },
                    { field: 'apiName', title: '接口名称', showOverflow: true },
                    { field: 'apiCode', title: '接口标识', showOverflow: true },
                    { field: 'apiUrl', title: '接口地址', showOverflow: true },
                    { field: 'apiPermission', title: '授权用户', showOverflow: true, visible: false },
                    { field: 'apiRequire', title: '接口要求', showOverflow: true, visible: false },
                    { field: 'apiFunction', title: '接口作用', showOverflow: true, visible: false },
                    { field: 'apiDoc', title: '接口对接说明', showOverflow: true, visible: false },
                    { title: '操作', width: 200, slots: { default: 'operate' }, align: 'center' }
                ],
                apiDefaultObj: {
                    tableName: '',
                    name: '',
                    desc: '',
                    permission: [],
                    require: {
                        condition: {},
                        operate: '',
                    },
                    conditionTemplate: {
                        filter: "'(name){=,string,var,and}': ''",
                        limit: "page_now: 0, page_size: 10",
                        sort: "id: 1, time: -1"
                    }
                },
                apiObj: {},
                isEdit: 0,
                addAPIModal: false,
                apiRules: {
                    name: [
                        { required: true, message: '请输入接口名称', trigger: 'blur' },
                        { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
                    ],
                    tableName: [
                        { required: true, message: '请选择操作的数据对象', trigger: 'change' }
                    ],
                    operate: [
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
                        {label: 'get', value: 'add'},
                        {label: 'get', value: 'delete'},
                        {label: 'get', value: 'update'},
                        {label: 'get', value: 'get'}
                    ]
                }
            }
        },
        created () {
            this.findList()
        },
        methods: {
            findList () {
                this.loading = true
                setTimeout(async () => {
                    this.loading = false
                    this.tablePage.total = 10
                    let res = await list()
                    console.log(res)
                    this.tableData = []
                }, 300)
            },
            toolbarButtonClickEvent ({ code }) {
                const $grid = this.$refs.xGrid
                switch (code) {
                    case 'addAPI':
                        // 深拷贝
                        this.apiObj = JSON.parse(JSON.stringify(this.apiDefaultObj))
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
                this.tableObj = JSON.parse(JSON.stringify(row))
                this.addTableModal = true
                this.isEdit = 1
            },
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


</style>
