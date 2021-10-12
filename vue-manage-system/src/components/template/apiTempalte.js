var apiTemplate = {
    model: "table_name",//操作对象
    desc: "接口描述",//接口描述
    name: "接口名称",
    createUser: "100000",//创建用户id
    permission:{
        user: "100000",//默认仅本人可看
        dept: "1000100"//该部门下所有人可看
    },//权限设置
    require:{
        operate: "put",//操作类型 post put delete get
        condition:{}//操作条件
    }//接口需求设置
}

var conditionTemplate = {
    put: {},//增
    post: {
        filter:{
            "(name){<>,string,and}": "jim",//name不等于jim
            "(age){>,int,and}": 20,//age > 20
            "(tel){%,string,and}": "%1234%",//tel包含1234
        },//过滤条件
        update:"field1,field2,field3",//修改字段
    },//改
    get: {
        filter:{
            "(name){<>,string,and}": "jim",//name不等于jim
            "(age){>,int,and}": 20,//age > 20
            "(tel){%,string,and}": "%1234%",//tel包含1234
        },//过滤条件
        sort: [{"time": 1}, {"id": -1}],
        limit: {"page_now": 0,"page_size": 10},
        return:"*"
    },//查
    delete: {
        filter:{
            "(name){<>,string,and}": "jim",//name不等于jim
            "(age){>,int,and}": 20,//age > 20
            "(tel){%,string,and}": "%1234%",//tel包含1234
        },//过滤条件
    },//删
}



//获取默认模板
function getDefaultTemplate(type) {
    switch (type) {
        case 'put':
            apiTemplate.require.operate = "put";
            apiTemplate.require.condition = conditionTemplate.put;
            break;
        case 'post':
            apiTemplate.require.operate = "post";
            apiTemplate.require.condition = conditionTemplate.post;
            break;
        case 'delete':
            apiTemplate.require.operate = "delete";
            apiTemplate.require.condition = conditionTemplate.delete;
            break;
        case 'get':
            apiTemplate.require.operate = "get";
            apiTemplate.require.condition = conditionTemplate.get;
            break;
        default:
            break;
    }
    return apiTemplate;
}

//将数据装载到模板，并且返回
function inItDataTemplate(data) {
    let template = getDefaultTemplate(data.type);
    template.model = data.tableName;
    template.name = data.name;
    template.desc = data.desc;
    return template;
}

//校验模板是否合法
function checkTemplate(template){
    let res = null;
    let dataType = typeof template;
    if(dataType == "string"){
        //字符串类型
        try{
            res = JSON.parse(template);
        }catch (e) {
            console.log("字符串转换JSON异常，格式有误!")
        }
    }else{
        //object类型
        res = template;
    }
    //这里预留校验数据内容是否合法
    return res;
}

export {getDefaultTemplate, inItDataTemplate, checkTemplate}