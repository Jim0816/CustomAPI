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
                        { code: 'addTable', name: '创建接口', status: 'primary', icon: 'fa fa-plus' },
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
                ]
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
                        {
                            id: 10001,
                            model: 'dept_id',
                            apiName: '按照部门名称查询部门信息',
                            apiCode: 'getDeptByDeptName',
                            apiUrl: 'https://127.0.0.1/dept/getDeptByDeptName',
                            apiType: 'get',
                            apiPermission: ['100001', '100006', '123000'],
                            apiRequire: {
                                condition:{
                                    filter:{},
                                    limit:{
                                        page_now:0,
                                        page_size:10
                                    },
                                    sort:{
                                    },
                                    return:"*"
                                },
                                operate:"get"
                            },
                            apiFunction: '接口功能描述',
                            apiDoc: '接口对接说明文档'
                        },
                        {
                            id: 10001,
                            model: 'dept_id',
                            apiName: '按照部门名称查询部门信息',
                            apiCode: 'getDeptByDeptName',
                            apiUrl: 'https://127.0.0.1/dept/getDeptByDeptName',
                            apiType: 'add',
                            apiPermission: ['100001', '100006', '123000'],
                            apiRequire: {
                                condition:{
                                    filter:{},
                                    limit:{
                                        page_now:0,
                                        page_size:10
                                    },
                                    sort:{
                                    },
                                    return:"*"
                                },
                                operate:"add"
                            },
                            apiFunction: '接口功能描述',
                            apiDoc: '接口对接说明文档'
                        },
                        {
                            id: 10001,
                            model: 'dept_id',
                            apiName: '按照部门名称查询部门信息',
                            apiCode: 'getDeptByDeptName',
                            apiUrl: 'https://127.0.0.1/dept/getDeptByDeptName',
                            apiType: 'update',
                            apiPermission: ['100001', '100006', '123000'],
                            apiRequire: {
                                condition:{
                                    filter:{},
                                    limit:{
                                        page_now:0,
                                        page_size:10
                                    },
                                    sort:{
                                    },
                                    return:"*"
                                },
                                operate:"update"
                            },
                            apiFunction: '接口功能描述',
                            apiDoc: '接口对接说明文档'
                        },
                        {
                            id: 10001,
                            model: 'dept_id',
                            apiName: '按照部门名称查询部门信息',
                            apiCode: 'getDeptByDeptName',
                            apiUrl: 'https://127.0.0.1/dept/getDeptByDeptName',
                            apiType: 'remove',
                            apiPermission: ['100001', '100006', '123000'],
                            apiRequire: {
                                condition:{
                                    filter:{},
                                    limit:{
                                        page_now:0,
                                        page_size:10
                                    },
                                    sort:{
                                    },
                                    return:"*"
                                },
                                operate:"remove"
                            },
                            apiFunction: '接口功能描述',
                            apiDoc: '接口对接说明文档'
                        },
                    ]
                }, 300)
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

</style>
