import request from '../utils/request';

export const allTables = (data) => {
    return request({
        url: '/api/table/tables',
        method: 'post',
        data
    });
};

export const getTables = (data) => {
    return request({
        url: '/api/table/get',
        method: 'post',
        data
    });
};

export const createTable = (data) => {
    return request({
        url: '/api/table/create',
        method: 'post',
        data
    });
};

export const dropTable = (data) => {
    return request({
        url: '/api/table/drop',
        method: 'post',
        data
    });
};

export const createApi = (data) => {
    return request({
        url: '/api/service/apply',
        method: 'post',
        data
    });
};

/*中文翻译为英文*/
export const transformZhToEn = query => {
    return request({
        url: '/transform',
        method: 'get',
        params: query
    });
};