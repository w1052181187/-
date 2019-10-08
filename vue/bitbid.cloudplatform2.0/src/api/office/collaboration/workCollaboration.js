import request from '@/utils/axios/components-axios'

export const workCollaboration = {
  /**
   * 查询工作协同列表接口
   */
  getList (query) {
    return request({
      url: '/work-collaboration',
      method: 'get',
      params: query
    })
  },
  /**
   * 查询单条工作协同接口
   */
  getById (objectId) {
    return request({
      url: '/work-collaboration/' + objectId,
      method: 'get'
    })
  },
  /**
   * 查询协同类型接口
   */
  getCollaborationTypes () {
    return request({
      url: '/work-collaboration/types',
      method: 'get'
    })
  },
  /**
   * 新增/修改工作协同接口
   */
  update (data) {
    return request({
      url: '/work-collaboration',
      method: 'put',
      data: data
    })
  },
  /**
   * 删除工作协同
   * @param {[long]} id [主键]
   */
  deleteById (id) {
    return request({
      url: '/work-collaboration/' + id,
      method: 'delete'
    })
  },
  updateToRead (data) {
    return request({
      url: '/work-collaboration/toRead',
      method: 'put',
      data: data
    })
  }
}
