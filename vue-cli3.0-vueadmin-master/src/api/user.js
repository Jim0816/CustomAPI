import axios from '@/config/httpConfig'

export function add(data) {
    return axios.post('/user/add', data)
}

export function update(data) {
    return axios.post('/user/update', data)
}

export function updatePassword(data) {
    return axios.post('/user/updatePassword', data)
}

export function remove(data) {
    return axios.post('/user/remove', data)
}
