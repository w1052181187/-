import request from '@/utils/axios/projectflow-axios'

export const bidSection = {
  /**
   * 查询标段接口
   */
  getList (query) {
    return request({
      url: '/bidSection-infos',
      method: 'get',
      params: query
    })
  },
  /**
   * 新增标段接口
   * @param {*} data [主体数据]
   */
  save (data) {
    return request({
      url: '/bidSection-infos',
      method: 'post',
      data: data
    })
  },
  /**
   * 删除标段接口
   * @param {[long]} id [主键]
   */
  delete (id) {
    return request({
      url: '/bidSection-infos/' + id,
      method: 'delete'
    })
  },
  /**
   * 查询某节点未关联的标段接口
   */
  getNotRelation (query) {
    return request({
      url: '/bidSection-infos/notRelation',
      method: 'get',
      params: query
    })
  },
  /**
   * 查询单条标段接口
   */
  getOne (code) {
    return request({
      url: '/bidSection-infos/' + code,
      method: 'get'
    })
  },
  /**
   * 查询未在任何公告中的标段信息接口
   */
  getNotUsedBidSection (tenderSystemCode) {
    return request({
      url: '/bidSection-infos/queryNotUsedBidSection/' + tenderSystemCode,
      method: 'get'
    })
  }
}
