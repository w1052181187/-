import request from '@/utils/axios/up-axios'

export const bulletinInfo = {

  /**
   * 查询公告接口
   */
  getList (query) {
    return request({
      url: '/bulletinInfo-infos',
      method: 'get',
      params: query
    })
  },
  /**
   * 新增公告接口
   * @param {*} data [主体数据]
   */
  save (data) {
    return request({
      url: '/bulletinInfo-infos',
      method: 'post',
      data: data
    })
  },
  /**
   * 查询单条公告接口
   * @param {[string]} code
   */
  getOne (code) {
    return request({
      url: '/bulletinInfo-infos/' + code,
      method: 'get'
    })
  },
  /**
   * 删除公告接口
   * @param {[long]} id [主键]
   */
  delete (id) {
    return request({
      url: '/bulletinInfo-infos/' + id,
      method: 'delete'
    })
  },
  /**
   * 当前标段下的公告列表查询
   */
  getByRelaSection (query) {
    return request({
      url: '/bulletinInfo-infos/relaSection',
      method: 'get',
      params: query
    })
  }
}
