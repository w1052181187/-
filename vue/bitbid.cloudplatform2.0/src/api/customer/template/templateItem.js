import request from '@/utils/axios/components-axios'

export const templateItem = {
  /** 查询列表 */
  queryList (params) {
    return request({
      url: `/evaluation-items`,
      method: 'get',
      params
    })
  },
  /** 根据主键查询 */
  queryOne (params) {
    return request({
      url: `/evaluation-items/${params}`,
      method: 'get'
    })
  },
  /** 保存/修改 */
  update (data) {
    return request({
      url: '/evaluation-items',
      method: 'post',
      data
    })
  },
  /** 删除 */
  logoff (objectId, parentId) {
    return request({
      url: `/evaluation-items/${objectId}/${parentId}`,
      method: 'delete'
    })
  },
  /** 查询所有的分组 */
  queryAllGroup (params) {
    return request({
      url: `/evaluation-items/queryAllGroup/${params}`,
      method: 'get'
    })
  },
  /** 验证重复 */
  isNoRepeated (params) {
    return request({
      url: `/evaluation-items/isNoRepeated`,
      method: 'get',
      params
    })
  }
}
