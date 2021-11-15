import axios from '@/config/httpConfig'

export function get() {
    return axios.get('/table/info')
}

export function list() {
    return axios.get('/table/list')
}

export function add(data) {
    return axios.post('/table/add', data)
}

export function update(data) {
    return axios.post('/table/update', data)
}

export function remove(id) {
    return axios.get(`/table/remove?id=${id}`)
}
