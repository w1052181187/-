export default [
  {
    path: '/archives/setting',
    name: '档案设置',
    meta: {
      title: '档案设置',
      active: '/archives/setting',
      permission: '1070301'
    },
    component: () => import(/* webpackChunkName: 'archives' */ '@/pages/archives/setting/index')
  },
  {
    path: '/archives/setting/file-catalog',
    name: '文件目录',
    meta: {
      title: '文件目录',
      active: '/archives/setting',
      permission: '1070101'
    },
    component: () => import(/* webpackChunkName: 'archives' */ '@/pages/archives/setting/file-catalog')
  }
]
