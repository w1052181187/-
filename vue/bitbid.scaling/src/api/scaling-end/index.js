import request from '@/utils/axios'

export const scalingEnd = {
  endScaling (projectId, query) {
    return request({
      url: '/result/endScalingsystem/' + projectId,
      params: query,
      method: 'post'
    })
  },
  getEndScaling (projectId, query) {
    return request({
      url: '/result/endScalingsystemPage/' + projectId,
      params: query,
      method: 'get'
    })
  },
  addVote (projectId, query) {
    return request({
      url: '/result/addNegoRound/' + projectId,
      params: query,
      method: 'post'
    })
  },
  // 根据项目id获取定标人信息
  getTenderUser (projectId, query) {
    return request({
      url: '/user/getById/' + projectId,
      params: query,
      method: 'get'
    })
  },
  getBidUserByRecord (projectId) {
    return request({
      url: '/biddingRecord/getBidUserByRecord/' + projectId,
      method: 'get'
    })
  },
  getRecord (params) {
    return request({
      url: '/biddingRecord',
      params: params,
      method: 'get'
    })
  },
  // 抽签室-投票排序
  submitOrderResult (data, query) {
    return request({
      url: '/result',
      params: query,
      data: data,
      method: 'post'
    })
  },
  getOrderResult (projectId, query) {
    return request({
      url: '/result/result/' + projectId,
      params: query,
      method: 'get'
    })
  },
  // 撤回-排序结果
  cancelOrderResult (projectId, query) {
    return request({
      url: '/result/cancel/' + projectId,
      params: query,
      method: 'post'
    })
  },
  // 定标人投票情况
  getScallingVoteResult (projectId, query) {
    return request({
      url: '/result/getResultByProjectId/' + projectId,
      params: query,
      method: 'get'
    })
  },
  // 投票汇总
  getScallingVoteReport (projectId, query) {
    return request({
      url: '/result/report/' + projectId,
      params: query,
      method: 'get'
    })
  },
  // 集体议事-获得聊天记录
  getChatRecord (query) {
    return request({
      url: '/websocketLog',
      params: query,
      method: 'get'
    })
  },
  // 竞价室-页面（流程）
  getBidRoomPage (projectId, query) {
    return request({
      url: '/result/roomPage/' + projectId,
      params: query,
      method: 'get'
    })
  },
  // 议事-页面（流程）
  getChatRoomPage (projectId, query) {
    return request({
      url: '/result/chatPage/' + projectId,
      params: query,
      method: 'get'
    })
  },
  // 投票室-抽签室-页面（流程）
  getVoteRoomPage (projectId, query) {
    return request({
      url: '/result/votingPage/' + projectId,
      params: query,
      method: 'get'
    })
  },
  // 释放就位状态
  removeReadyUser (projectId) {
    return request({
      url: '/result/removeReadyUser/' + projectId,
      method: 'get'
    })
  },
  // 组长汇总-确定投票结果
  confirmResult (projectId, query) {
    return request({
      url: '/result/confirmResult/' + projectId,
      params: query,
      method: 'post'
    })
  }
}
