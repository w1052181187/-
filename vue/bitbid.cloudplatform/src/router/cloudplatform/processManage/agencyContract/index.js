export default [
  {
    path: '/processManage/agencyContract',
    name: 'agencyContract-list',
    meta: {
      title: '代理合同管理'
    },
    component: resolve => require(['@/pages/processManage/agencyContract/index'], resolve)
  },
  {
    path: '/processManage/agencyContract/add',
    name: 'agencyContract-add',
    meta: {
      title: '添加代理合同'
    },
    component: resolve => require(['@/pages/processManage/agencyContract/add'], resolve)
  },
  {
    path: '/processManage/agencyContract/update/:code',
    name: 'agencyContract-update',
    meta: {
      title: '修改代理合同'
    },
    component: resolve => require(['@/pages/processManage/agencyContract/update'], resolve)
  },
  {
    path: '/processManage/agencyContract/detail/:code',
    name: 'agencyContract-update',
    meta: {
      title: '查看代理合同'
    },
    component: resolve => require(['@/pages/processManage/agencyContract/detail'], resolve)
  }
]
