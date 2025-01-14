import {titleName} from '@/assets/js/common'
export default[
  {
    path: '/index/account',
    name: 'account',
    meta: {
      roleIds: [999],
      title: '账号管理' + titleName,
      active: '/index/account'
    },
    component: resolve => require(['@/pages/sys-manage/account'], resolve)
  },
  {
    path: '/index/role',
    name: 'role',
    meta: {
      roleIds: [999],
      title: '角色管理' + titleName,
      active: '/index/role'
    },
    component: resolve => require(['@/pages/sys-manage/role'], resolve)
  },
  {
    path: '/index/assign-power/:roleId',
    name: 'assign',
    meta: {
      roleIds: [999],
      title: '分配角色' + titleName,
      active: '/index/role'
    },
    component: resolve => require(['@/pages/sys-manage/assign-power'], resolve)
  }
]
