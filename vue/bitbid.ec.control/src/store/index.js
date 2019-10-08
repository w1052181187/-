/**
 * Created by Qinzy on 2018/11/9.
 */
import Vuex from 'vuex'
import Vue from 'vue'
import {login} from '@/api'
import {getToken, setToken, removeToken} from '@/utils/auth'

Vue.use(Vuex)

const state = {
  // 用户信息：{userId: xxx, enterpriseId: xxx, userName: xxx, enterpriseName: xxx, userType: xxx}
  authUser: '',
  // permissions: '',
  token: getToken()
}

const getters = {
  authUser: state => state.authUser,
  token: state => state.token
  // permissions: state => state.permissions
}

const mutations = {
  SET_USER: (state, user) => {
    state.authUser = user
    // window.allCommentMethod.Change_User()
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  }
  // SET_PERMISSIONS: (state, permissions) => {
  // state.permissions = permissions
  // }
}

const actions = {
  /** 登录 */
  Login: ({commit}, userInfo) => {
    return new Promise((resolve, reject) => {
      login.login(userInfo).then(response => {
        // console.log(response)
        // 登录成功
        if (response.data.resCode === '0000') {
          // state保存token
          commit('SET_TOKEN', response.data.token)
          // 保存token到cookie中
          setToken(response.data.token)
          resolve(response.data)
        } else {
          reject(response.data.resCode)
        }
      })
    })
  },
  /** 获取用户信息 */
  GetLoginInfo: ({commit, state}) => {
    return new Promise((resolve, reject) => {
      // TODO 目前先不考虑根据模块获取权限列表
      login.getLoginInfo(state.token).then(response => {
        // console.log(response)
        // 保存用户信息放到state中
        commit('SET_USER', response.data.loginUserInfo)
        // 保存权限信息到state中
        // commit('SET_PERMISSIONS', response.data.permissions)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  /** 退出 */
  Logout: ({dispatch, commit}) => {
    return new Promise((resolve, reject) => {
      login.logout().then(response => {
        // 清除数据
        dispatch('ClearLoginInfo')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },
  /** 清除数据 */
  ClearLoginInfo: ({commit}) => {
    // 清除数据
    commit('SET_USER', '')
    commit('SET_TOKEN', '')
    // commit('SET_PERMISSIONS', '')
    // 清除cookie中的token
    removeToken()
  },
  Register: ({commit}, userInfo) => {
    return new Promise((resolve, reject) => {
      login.register(userInfo).then(response => {
        // 注册成功
        if (response.data.resCode === '0000') {
          // state保存token
          commit('SET_TOKEN', response.data.token)
          // 保存token到cookie中
          setToken(response.data.token)
          resolve()
        } else {
          reject(response.data.resCode)
        }
      }).catch(error => {
        reject(error)
      })
    })
  }
}

export default new Vuex.Store({
  state,
  getters,
  mutations,
  actions
})
