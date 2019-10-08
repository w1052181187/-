import request from '@/utils/axios/projectflow-axios'

export const tenderProject = {
  /**
   * 查询招标项目接口
   */
  getList (query) {
    return request({
      url: '/tenderProject-infos',
      method: 'get',
      params: query
    })
  },
  /**
   * 查询单条招标项目接口
   */
  getOne (code) {
    return request({
      url: '/tenderProject-infos/' + code,
      method: 'get'
    })
  },
  /**
   * 新增招标项目接口
   * @param {*} data [主体数据]
   */
  save (data) {
    return request({
      url: '/tenderProject-infos',
      method: 'post',
      data: data
    })
  },
  /**
   * 删除招标项目接口
   * @param {[long]} id [主键]
   */
  delete (id) {
    return request({
      url: '/tenderProject-infos/' + id,
      method: 'delete'
    })
  },
  /**
   * 添加详情时展示部分概况信息接口
   */
  getBaseByCode (code) {
    return request({
      url: '/tenderProject-infos/queryBaseByCode/' + code,
      method: 'get'
    })
  },
  /**
   * 根据标段code获取对应的招标项目信息
   */
  getByBidSectionCode (bidSectionCode) {
    return request({
      url: '/tenderProject-infos/queryByBidSectionCode/' + bidSectionCode,
      method: 'get'
    })
  }
}
