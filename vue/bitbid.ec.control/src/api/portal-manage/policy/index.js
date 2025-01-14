import request from '@/utils/axios'

export const policy = {
  queryList (params) {
    return request({
      url: '/news',
      method: 'get',
      params
    })
  },
  queryOne (params) {
    return request({
      url: `/news/queryByObjectId/${params}`,
      method: 'get'
    })
  },
  update (data) {
    return request({
      url: '/news',
      method: 'post',
      data
    })
  },
  deletePolicy (params) {
    return request({
      url: `/news/${params}`,
      method: 'delete'
    })
  }
}
