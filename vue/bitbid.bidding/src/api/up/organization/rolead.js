/**
 * Created by lixuqiang on 2018/6/4.
 */
/**
 * Created by lixuqiang on 2018/5/24.
 */
import request from '@/utils/axios/up-axios'

export const rolead = {
  roleadList (query) {
    return request({
      url: '/roles',
      method: 'get',
      params: query
    })
  },
  roleadAdd (query) {
    return request({
      url: '/roles',
      method: 'post',
      data: query
    })
  },
  roleadEditlook (query) {
    return request({
      url: `/roles/${query}?_t=${new Date().getTime()}`,
      method: 'get'
    })
  },
  roleadEdit (query) {
    return request({
      url: '/roles',
      method: 'put',
      data: query
    })
  },
  roleadDelet (query) {
    return request({
      url: '/roles/' + query,
      method: 'delete'
    })
  },
  distrJuris (query) {
    return request({
      url: '/module-managements/' + query,
      method: 'get'
    })
  },
  roleadSeacher (query) {
    return request({
      url: '/roles',
      method: 'get',
      params: query
    })
  },
  // 分配权限列表
  distrjurisList (query) {
    return request({
      url: '/permissions',
      method: 'get',
      params: query
    })
  },
  // 数据范围列表
  numberList  (query) {
    return request({
      url: '/departments/' + query,
      method: 'get'
    })
  },
  // 权限范围列表
  jurisList  (query) {
    return request({
      url: '/limitsJurisdictions',
      method: 'get',
      params: query
    })
  },
  // 分配权限
  addjuris  (query) {
    return request({
      url: '/limitsJurisdictions',
      method: 'post',
      data: query
    })
  },
  // 修改权限
  editjuris  (query) {
    return request({
      url: '/limitsJurisdictions',
      method: 'put',
      data: query
    })
  },
  // 角色管理中的启用和禁用的功能
  disableEnable (query) {
    return request({
      url: '/limitsJurisdictions/disable/' + query,
      method: 'put'
    })
  }

}
