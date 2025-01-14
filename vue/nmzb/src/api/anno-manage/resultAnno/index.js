import request from '@/utils/axios'

export const resultAnno = {
  queryList (query) {
    return request({
      url: '/bulletins',
      params: query,
      method: 'get'
    })
  },
  save (data) {
    return request({
      url: '/winBidBulletins',
      data: data,
      method: 'post'
    })
  },
  detail (objectId) {
    return request({
      url: '/winBidBulletins/' + objectId,
      method: 'get'
    })
  },
  update (data) {
    return request({
      url: '/winBidBulletins',
      data: data,
      method: 'put'
    })
  },
  remove (objectId) {
    return request({
      url: '/winBidBulletins/' + objectId,
      method: 'delete'
    })
  },
  approve (data) {
    return request({
      url: '/winBidBulletins/approve',
      data: data,
      method: 'put'
    })
  },
  change (data) {
    return request({
      url: '/winBidBulletins/change',
      data: data,
      method: 'post'
    })
  }
}
