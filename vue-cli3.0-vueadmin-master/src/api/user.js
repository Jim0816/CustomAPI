import axios from '@/config/httpConfig'

export function add(data) {
    return axios.post('/user/create', data)
}
