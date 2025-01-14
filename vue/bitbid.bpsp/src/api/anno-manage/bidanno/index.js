import request from '@/utils/axios'

export const bidanno = {
  queryList (query) {
    return request({
      url: '/bulletins',
      params: query,
      method: 'get'
    })
  },
  save (data) {
    return request({
      url: '/tenderBulletins',
      data: data,
      method: 'post'
    })
  },
  detail (objectId) {
    return request({
      url: '/tenderBulletins/' + objectId,
      method: 'get'
    })
  },
  update (data) {
    return request({
      url: '/tenderBulletins',
      data: data,
      method: 'put'
    })
  },
  remove (objectId) {
    return request({
      url: '/tenderBulletins/' + objectId,
      method: 'delete'
    })
  },
  approve (data) {
    return request({
      url: '/tenderBulletins/approve',
      data: data,
      method: 'put'
    })
  },
  withdraw (objectId, code) {
    return request({
      url: '/tenderBulletins/withdraw/' + objectId + '/' + code,
      method: 'put'
    })
  }
}
