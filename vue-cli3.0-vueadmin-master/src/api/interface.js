import axios from '@/config/httpConfig'

export function get() {
    return axios.get('/interface/info')
}

export function list() {
    return axios.get('/interface/list')
}

export function templates() {
    return axios.get('/interface/templates')
}

export function add(data) {
    return axios.post('/interface/add', data)
}

export function update(data) {
    return axios.post('/interface/update', data)
}

export function remove(id) {
    return axios.get(`/interface/remove?id=${id}`)
}
