import axios from 'axios'

export const http = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

export interface ApiResult<T> {
  code: number
  msg: string
  data: T
}

http.interceptors.response.use(
  (res) => res,
  (err) => Promise.reject(err),
)

export async function unwrap<T>(p: Promise<{ data: ApiResult<T> }>): Promise<T> {
  const { data } = await p
  if (data.code !== 200) {
    throw new Error(data.msg || '请求失败')
  }
  return data.data
}
