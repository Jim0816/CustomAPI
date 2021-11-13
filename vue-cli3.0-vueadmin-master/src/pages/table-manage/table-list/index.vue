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
                    <vxe-button :disabled="row.tableName.indexOf('sys_') != -1 ? true : false" icon="fa fa-edit" title="编辑" circle @click="editTable(row)"></vxe-button>
                    <vxe-button :disabled="row.tableName.indexOf('sys_') != -1 ? true : false" icon="fa fa-trash" title="删除" circle @click="removeTable(row)"></vxe-button>
                    <vxe-button icon="fa fa-eye" title="查看" circle @click="showTable(row)"></vxe-button>
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
            <vxe-modal v-model="addTableModal" title="编辑数据对象" width="1000" :before-hide-method="confirmCloseModal" show-zoom resize transfer >
                <template #default>
                    <div class="addTable-container">
                        <vxe-grid
                            border
                            resizable
                            keep-source
                            ref="xGrid_2"
                            id="toolbar_demo_2"
                            height="400"
                            :print-config="{}"
                            :import-config="{}"
                            :export-config="{}"
                            :columns="fieldsTableColumn"
                            :toolbar-config="fieldsTableToolbar"
                            :data="tableObj.fields"
                            @toolbar-button-click="fieldToolbarButtonClickEvent"
                            @toolbar-tool-click="fieldToolbarToolClickEvent">
                            <!--操作行-->
                            <template #operate="{ row }">
                                <vxe-button icon="fa fa-edit" title="编辑" circle @click="editField(row)"></vxe-button>
                                <vxe-button icon="fa fa-trash" title="删除" circle @click="removeField(row)"></vxe-button>
                            </template>
                        </vxe-grid>
                    </div>
                </template>
            </vxe-modal>

            <!--添加字段弹框-->
            <vxe-modal v-model="addFieldModal" title="编辑数据对象-字段" width="600" height="420" show-zoom resize remember>
                <template #default>
                    <vxe-form :data="fieldForm" :rules="fieldRule" @submit="addFieldSubmit" title-align="right" title-width="80">
                        <vxe-form-item title="字段名称" field="name" span="24" :item-render="{name: 'input', attrs: {placeholder: '请输入字段名称'}}"></vxe-form-item>
                        <vxe-form-item title="数据类型" field="type" span="12" :item-render="{name: '$select', options: fieldTypeList}"></vxe-form-item>
                        <vxe-form-item title="数据长度" field="length" span="12" :item-render="{name: 'input', attrs: {type: 'number', placeholder: '请输入数据最大长度'}}"></vxe-form-item>
                        <vxe-form-item title="必填字段" field="isRequire" span="12" :item-render="{name: '$radio', options: [{ label: '是', value: 1 }, { label: '否', value: 0 }]}"></vxe-form-item>
                        <vxe-form-item title="唯一字段" field="isUnique" span="12" :item-render="{name: '$radio', options: [{ label: '是', value: 1 }, { label: '否', value: 0 }]}"></vxe-form-item>
                        <vxe-form-item title="默认值" field="default" span="24" :item-render="{name: 'input', attrs: {placeholder: '请输入字段默认值'}}"></vxe-form-item>
                        <vxe-form-item title="备注" field="remark" span="24" :item-render="{}">
                            <template #default="params">
                                <vxe-textarea v-model="fieldForm.remark" placeholder="请输入字段备注" :autosize="{minRows: 3, maxRows: 6}" clearable></vxe-textarea>
                            </template>
                        </vxe-form-item>
                        <vxe-form-item align="center" span="24">
                            <template #default>
                                <vxe-button type="submit" status="primary">提交</vxe-button>
                                <vxe-button @click="addFieldReset">重置</vxe-button>
                            </template>
                        </vxe-form-item>
                    </vxe-form>
                </template>
            </vxe-modal>

            <!--添加数据对象基本信息填写弹框-->
            <vxe-modal v-model="addTableBasicInfoModal" title="编辑数据对象-基本信息" width="600" height="420" show-zoom resize remember>
                <template #default>
                    <vxe-form :data="tableObj" :rules="tableRule" @submit="addTableSubmit" title-align="right" title-width="80">
                        <vxe-form-item title="表名称" field="tableName" span="24" :item-render="{name: 'input', attrs: {placeholder: '请输入字段名称'}}"></vxe-form-item>
                        <vxe-form-item title="数据类型" field="dbType" span="12" :item-render="{name: '$select', options: [{ label: 'MongoDB', value: 'MongoDB' }, { label: 'Mysql', value: 'Mysql' }]}"></vxe-form-item>
                        <vxe-form-item title="数据描述" field="desc" span="24" :item-render="{}">
                            <template #default="params">
                                <vxe-textarea v-model="tableObj.desc" placeholder="请输入数据对象描述信息" :autosize="{minRows: 3, maxRows: 6}" clearable></vxe-textarea>
                            </template>
                        </vxe-form-item>
                        <vxe-form-item align="center" span="24">
                            <template #default>
                                <vxe-button type="submit" status="primary">提交</vxe-button>
                                <vxe-button @click="addTableReset">重置</vxe-button>
                            </template>
                        </vxe-form-item>
                    </vxe-form>
                </template>
            </vxe-modal>

            <!--查看数据对象信息弹框-->
            <vxe-modal v-model="showTableModal" title="查看数据对象信息" width="600" height="420" show-zoom resize remember>
                <template #default>
                    <div class="show-table-info-div">
                        <div class="show-table-info-basic">
                            <vxe-form :data="showTableObj" title-align="right" title-width="80">
                                <!--基本信息-->
                                <vxe-form-item title="基本信息" span="24" title-align="left" title-width="200px" :title-prefix="{icon: 'el-icon-s-order'}"></vxe-form-item>
                                <vxe-form-item title="表名" field="desc" span="12" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showTableObj.tableName" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                                <vxe-form-item title="存储类型" field="dbType" span="12" :item-render="{}">
                                    <template #default="params">
                                        <vxe-input v-model="showTableObj.dbType" readonly></vxe-input>
                                    </template>
                                </vxe-form-item>
                                <vxe-form-item title="存储类型" field="desc" span="24" :item-render="{}">
                                    <template #default="params">
                                        <vxe-textarea v-model="showTableObj.desc"  :autosize="{minRows: 3, maxRows: 6}" readonly></vxe-textarea>
                                    </template>
                                </vxe-form-item>
                            </vxe-form>
                        </div>

                        <!--字段信息-->
                        <div class="show-table-info-fields">
                            <vxe-form :data="showTableObj" title-align="right" title-width="80">
                                <vxe-form-item title="字段信息" span="24" title-align="left" title-width="200px" :title-prefix="{icon: 'el-icon-s-grid'}"></vxe-form-item>
                            </vxe-form>
                            <div class="field-div" v-for="item in showTableObj.fields">
                                {{item}}
                            </div>
                        </div>

                        <!--其他信息-->
                        <div class="show-table-info-other">
                            <vxe-form :data="showTableObj" title-align="right" title-width="80">
                                <vxe-form-item title="其他信息" span="24" title-align="left" title-width="200px" :title-prefix="{icon: 'el-icon-s-grid'}"></vxe-form-item>
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
    import { get, list, add, update, remove } from '@/api/table'
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
                    { code: 'addTable', name: '新增', status: 'primary', icon: 'fa fa-plus' },
                    { code: 'myBatchDel', name: '批量删除', status: 'danger', icon: 'el-icon-delete' }
                ],
                refresh: true,
                import: true,
                export: true,
                print: true,
                zoom: true,
                custom: true
            },
            tableColumn: [
                { type: 'checkbox', width: 50 },
                { type: 'seq', width: 60 },
                { field: 'tableName', title: '表名', showOverflow: true },
                { field: 'dbType', title: '存储类型', showOverflow: true },
                { field: 'desc', title: '数据描述', showOverflow: true },
                //{ field: 'fields', title: '字段列表', showOverflow: true },
                { title: '操作', width: 200, slots: { default: 'operate' }, align: 'center' }
            ],
            showTableModal: false,
            addTableModal: false,
            tableDefaultObj: {
                tableName: '',
                dbType: 'MongoDB',
                desc: '',
                fields: []
            },
            tableObj: {},
            showTableObj: {},
            fieldsTableColumn: [
                { type: 'seq', width: 60 },
                { field: 'name', title: '字段名称', showOverflow: true },
                { field: 'type', title: '数据类型', showOverflow: true },
                { field: 'length', title: '长度', showOverflow: true },
                { field: 'isRequire', title: '必填项', showOverflow: true },
                { field: 'isUnique', title: '唯一项', showOverflow: true },
                { field: 'default', title: '默认值', showOverflow: true },
                { field: 'remark', title: '备注', showOverflow: true },
                { title: '操作', width: 200, slots: { default: 'operate' }, align: 'center' }
            ],
            fieldsTableToolbar: {
                buttons: [
                    { code: 'addField', name: '新增字段', status: 'primary', icon: 'fa fa-plus' },
                    { code: 'saveTable', name: '提交数据', status: 'success', icon: 'fa fa-save' },
                ],
                refresh: true,
                import: true,
                export: true,
                print: true,
                zoom: true,
                custom: true
            },
            addFieldModal: false,
            editFieldInfo: {
                status: false, //默认不编辑
                index: '', // 编辑项在原数组中的索引
            },
            fieldDefaultObj: {
                name: '',
                type: 'String',
                length: 50,
                isRequire: 0,
                isUnique: 0,
                default: '',
                remark: ''
            },
            fieldForm: {},
            tableRule: {
                tableName: [
                    { required: true, message: '请输入表（数据对象）名称' },
                    { min: 2, max: 20, message: '长度在 2 到 20 个字符' }
                ],
                dbType: [
                    { required: true, message: '请选择表（数据对象）的存储方式' }
                ]
            },
            fieldRule: {
                name: [
                    { required: true, message: '请输入字段名称' },
                    { min: 2, max: 20, message: '长度在 2 到 20 个字符' }
                ],
                type: [
                    { required: true, message: '请选择字段数据类型' }
                ],
                isRequire: [
                    { required: true, message: '请选择字段是否必须' }
                ],
                isUnique: [
                    { required: true, message: '请选择字段是否唯一' }
                ]
            },
            addTableBasicInfoModal: false,
            // 数据字典
            fieldTypeList: [
                { label: 'String', value: 'String' },
                { label: 'Int', value: 'Int' }
            ]
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
                this.tableData = res.data
            }, 100)
        },
        searchEvent () {
            this.tablePage.currentPage = 1
            this.findList()
        },
        handlePageChange ({ currentPage, pageSize }) {
            this.tablePage.currentPage = currentPage
            this.tablePage.pageSize = pageSize
            this.findList()
        },
        editTable(row) {
            // 深拷贝
            this.tableObj = JSON.parse(JSON.stringify(row))
            this.addTableModal = true
        },
        removeTable(row) {
            this.$confirm('是否要删除此项数据, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                /*const $grid = this.$refs.xGrid
                const selectRecords = $grid.getRowIndex(row)
                console.log(selectRecords)*/
                let res = this.removeTableSubmit(row)
                if(res) {
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
        removeTableSubmit(row){
            // 通过table的id删除实体,访问后端
            return true
        },
        showTable(row) {
            console.log(row)
            this.showTableObj = row
            this.showTableModal = true
        },
        editField(row) {
            // 深拷贝
            this.fieldForm = JSON.parse(JSON.stringify(row))
            const $grid = this.$refs.xGrid_2
            let index = $grid.getRowIndex(row)
            this.editFieldInfo = {
                status: true, //状态改为编辑
                index: index, // 编辑项在原数组中的索引
            }
            this.addFieldModal = true
        },
        removeField(row) {
            const $grid = this.$refs.xGrid_2
            let index = $grid.getRowIndex(row)
            this.tableObj.fields.splice(index,1);
        },
        toolbarButtonClickEvent ({ code }) {
            const $grid = this.$refs.xGrid
            switch (code) {
            case 'addTable':
                // 深拷贝
                this.tableObj = JSON.parse(JSON.stringify(this.tableDefaultObj))
                this.addTableModal = true
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
                }).then(() => {
                    for(let i = 0; i < selectRecords.length; i++){
                        let res = this.removeTableSubmit(selectRecords[i])
                        if(!res){
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
        async confirmCloseModal() {
            // 由于原来组件封装好的方法有异常，只能这样处理
            let selectRes = await this.$confirm('此操作将放弃本次编辑的数据, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                return true
            }).catch(() => {
                return false
            })

            // eslint-disable-next-line eqeqeq
            if (selectRes == false) {
                return new Error()
            }
        },
        async fieldToolbarButtonClickEvent ({ code }) {
            const $grid = this.$refs.xGrid
            switch (code) {
            case 'addField':
                // 深拷贝
                this.fieldForm = JSON.parse(JSON.stringify(this.fieldDefaultObj))
                this.addFieldModal = true
                break
            case 'saveTable':
                // 准备填写表基本信息
                if(this.tableObj.fields.length > 0){
                    console.log(this.tableObj)
                    const res = await add(this.tableObj)
                    if(res.data){
                        //创建成功
                        this.$message({
                            message: '创建成功',
                            type: 'success'
                        });
                    }else{
                        //创建失败
                        this.$message({
                            message: '创建失败',
                            type: 'error'
                        });
                    }
                    this.addTableBasicInfoModal = true
                }else{
                    this.$message({
                        message: '请完成字段信息填写',
                        type: 'warning'
                    });
                }
                break
            case 'myExport':
                $grid.exportData({
                    type: 'csv'
                })
                break
            }
        },
        fieldToolbarToolClickEvent ({ code }) {
            const $grid = this.$refs.xGrid
            switch (code) {
            case 'myPrint':
                $grid.print()
                break
            }
        },
        addFieldSubmit() {
            if(this.editFieldInfo.status){
                // true 表示执行编辑修改操作
                this.tableObj.fields[this.editFieldInfo.index] = this.fieldForm
                // 改为默认状态
                this.editFieldInfo.status = false
                console.log(this.tableObj.fields)
                const $grid = this.$refs.xGrid_2
                $grid.reloadData(this.tableObj.fields)
            }else{
                // 新增
                this.tableObj.fields.push(this.fieldForm)
            }
            this.addFieldModal = false
        },
        addFieldReset() {
            this.fieldForm = JSON.parse(JSON.stringify(this.fieldDefaultObj))
        },
        async addTableSubmit() {
            let data = this.tableObj
            console.log(data)
            if (this.checkDataBeforeSubmit(data)) {
                // 数据校验成功，准备提交后端
                //let res = await add(this.loginForm)
                //console.log(res)
                // 刷新数据对象列表
            }
        },
        addTableReset() {
            let fields = this.tableObj.fields
            this.tableObj = JSON.parse(JSON.stringify(this.tableDefaultObj))
            this.tableObj.fields = fields
        },
        // 提交前，校验数据
        checkDataBeforeSubmit(data) {
            // eslint-disable-next-line eqeqeq
            if (data.tableName != '' && data.fields != undefined && data.fields.length > 0 && data.dataType != '') {
                // 继续校验字段列表
                for (let i = 0; i < data.fields.length; i++) {
                    let fieldObj = data.fields[i]
                    // 名称校验
                    // eslint-disable-next-line eqeqeq
                    if (fieldObj.name == undefined || fieldObj.name == '') {
                        console.log('字段名称校验失败!')
                        return false
                    }
                    // 长度校验
                    // eslint-disable-next-line eqeqeq
                    if (fieldObj.length == undefined || fieldObj.length == '' || fieldObj.length > 300 || fieldObj.length < -1) {
                        console.log('字段长度校验失败!')
                        return false
                    }
                    // 类型校验
                    // eslint-disable-next-line eqeqeq
                    if (fieldObj.type == undefined || fieldObj.type == '') {
                        console.log('字段类型校验失败!')
                        switch (fieldObj.type) {
                        case 'String':
                            if (fieldObj.length < 0 || fieldObj.length > 300) {
                                return false
                            }
                            break
                        case 'Int':
                            if (fieldObj.length < 0 || fieldObj.length > 300) {
                                return false
                            }
                            break
                        default:
                            // eslint-disable-next-line eqeqeq
                            if (fieldObj.length != -1) {
                                return false
                            }
                            break
                        }
                        return false
                    }
                    // 必填项校验
                    // eslint-disable-next-line eqeqeq
                    if (fieldObj.isRequire == undefined || (fieldObj.isRequire != 0 && fieldObj.isRequire != 1)) {
                        console.log('字段必填项校验失败!')
                        return false
                    }
                    // 唯一项校验
                    // eslint-disable-next-line eqeqeq
                    if (fieldObj.isUnique == undefined || (fieldObj.isUnique != 0 && fieldObj.isUnique != 1)) {
                        console.log('字段唯一项校验失败!')
                        return false
                    }
                }
            }
            return true
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

    .addTable-container{
        position: relative;
        width: 100%;
        height: 400px;
        overflow: auto;
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
