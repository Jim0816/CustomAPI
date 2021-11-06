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
                    <vxe-button icon="fa fa-edit" title="编辑" circle @click="editRowEvent(row)"></vxe-button>
                    <vxe-button icon="fa fa-trash" title="删除" circle @click="removeRowEvent(row)"></vxe-button>
                    <vxe-button icon="fa fa-eye" title="查看" circle></vxe-button>
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
            <vxe-modal v-model="addTableModal" width="1000"  show-footer show-zoom resize transfer>
                <template #title>
                    <span>新建数据对象</span>
                </template>
                <template #default>
                    <div class="addTable-container"></div>
                </template>
            </vxe-modal>
        </div>
        <div class="page-container-bottom"></div>
    </div>
</template>


<script>
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
                    { code: 'myInsert', name: '新增', status: 'primary', icon: 'fa fa-plus' },
                    { code: 'mySave', name: '保存', status: 'success', icon: 'fa fa-save' },
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
                { title: '操作', width: 200, slots: { default: 'operate' }, align: 'center' }
            ],
            addTableModal: false
        }
    },
    created () {
        this.findList()
    },
    methods: {
        findList () {
            this.loading = true
            setTimeout(() => {
                this.loading = false
                this.tablePage.total = 10
                this.tableData = [
                    { id: 10001, tableName: 'Test1', dbType: 'MongoDB', desc: '将列固定在左侧或者右侧（注意：固定列应该放在左右两侧的位置）', sex: '1', age: 28, address: 'Shenzhen' },
                    { id: 10002, tableName: 'Test2', dbType: 'MongoDB', desc: '将列固定在左侧或者右侧（注意：固定列应该放在左右两侧的位置）', sex: '0', age: 22, address: 'Guangzhou' },
                ]
            }, 300)
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
        toolbarButtonClickEvent ({ code }) {
            const $grid = this.$refs.xGrid
            switch (code) {
            case 'myInsert':
                this.addTableModal = true;
                break
            case 'mySave':
                setTimeout(() => {
                    const { insertRecords, removeRecords, updateRecords } = $grid.getRecordset()
                    this.$XModal.message({ content: `新增 ${insertRecords.length} 条，删除 ${removeRecords.length} 条，更新 ${updateRecords.length} 条`, status: 'success' })
                    this.loadData()
                }, 100)
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
    }
</style>
