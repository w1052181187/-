export default[
  {
    path: '/system/roles',
    name: '角色管理',
    meta: {
      title: '角色管理',
      active: '/system/roles',
      permission: 'admin'
    },
    component: () => import(/* webpackChunkName: 'system' */ '@/pages/system/roles/index')
  },
  {
    path: '/system/roles/assignPermissions',
    name: '角色管理-分配权限',
    meta: {
      title: '角色管理-分配权限',
      active: '/system/roles',
      permission: 'admin'
    },
    component: () => import(/* webpackChunkName: 'system' */ '@/pages/system/roles/assign_permissions')
  }
]
