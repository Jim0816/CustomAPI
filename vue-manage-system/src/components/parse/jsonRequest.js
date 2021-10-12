// 请求数据模板  (备用)
var baseTemplate = {
    service: '',
    data: {}
}

/**
 * 封装请求数据 (暂时未使用)
 * service 业务类型
 * data 当前业务类型请求数据
 * */
function formatRequest(service, data) {
    switch (service) {
        case 'create':
            //创建数据对象
            baseTemplate.data = data;
            baseTemplate.service = service;
            break;
        case 'select':
            //创建api
            baseTemplate.data = data;
            baseTemplate.service = service;
            break;
        case 'apply':
            //创建数据对象
            baseTemplate.data = data;
            baseTemplate.service = service;
            break;
        default:
            break;
    }
    return baseTemplate;
}



export {formatRequest}