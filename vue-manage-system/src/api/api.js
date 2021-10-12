import request from '../utils/request';

export const getApis = (data) => {
    return request({
        url: '/api/interface/get',
        method: 'post',
        data
    });
};

export const create = (data) => {
    return request({
        url: '/api/interface/create',
        method: 'post',
        data
    });
};

export const update = (data) => {
    return request({
        url: '/api/interface/update',
        method: 'post',
        data
    });
};