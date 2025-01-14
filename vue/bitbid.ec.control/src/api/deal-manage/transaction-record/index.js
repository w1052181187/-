import request from '@/utils/axios'

export const transactionRecord = {
  /**
   * 列表查询
   * @param {*} query [查询信息]
   */
  getList (query) {
    return request({
      url: '/Transaction-record',
      method: 'get',
      params: query
    })
  },
  /**
   * 单条查询
   * @param id
   */
  getOne (id) {
    return request({
      url: '/Transaction-record/query/' + id,
      method: 'get'
    })
  },
  /**
   * 新增接口
   * @param {*} data [主体数据]
   */
  save (data) {
    return request({
      url: '/Transaction-record',
      method: 'post',
      data: data
    })
  },
  /**
   * 平台统计列表查询
   * @param {*} query [查询信息]
   */
  getCountList (query) {
    return request({
      url: '/Transaction-record/count',
      method: 'get',
      params: query
    })
  },
  /**
   * 平台统计列表查询（采购人）
   * @param {*} query [查询信息]
   */
  getCountListByBuyer (query) {
    return request({
      url: '/Transaction-record/ecPlatStatistics',
      method: 'get',
      params: query
    })
  },
  /**
   * 采购人统计列表查询
   * @param {*} query [查询信息]
   */
  getProcurementList (query) {
    return request({
      url: '/Transaction-record/countProcurement',
      method: 'get',
      params: query
    })
  },
  /**
   * 平台统计列表查询
   * @param {*} query [查询信息]
   */
  countExportExcel (query) {
    return request({
      url: '/Transaction-record/exportExcel',
      method: 'get',
      params: query
    })
  },
  /**
   * 采购人统计列表查询
   * @param {*} query [查询信息]
   */
  countProcurementExportExcel (query) {
    return request({
      url: '/Transaction-record/exportProcurementExcel',
      method: 'get',
      params: query
    })
  },
  /**
   * 采购人消费统计查询
   * @param {*} query [查询信息]
   */
  countProcurementByPlatformId (query) {
    return request({
      url: '/Transaction-record/countProcurementByPlatformId',
      method: 'get',
      params: query
    })
  },
  countBuyerStatistics (params) {
    return request({
      url: '/Transaction-record/buyerStatistics',
      method: 'get',
      params
    })
  }
}
