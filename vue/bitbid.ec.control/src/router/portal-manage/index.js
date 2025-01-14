import {titleName} from '@/assets/js/common'
export default[
  {
    path: '/index/deal-notice',
    name: 'deal-notice',
    meta: {
      roleIds: [999],
      title: '成交公示管理' + titleName,
      active: '/index/deal-notice'
    },
    component: resolve => require(['@/pages/portal-manage/deal-notice'], resolve)
  },
  {
    path: '/index/except-notice',
    name: 'except-notice',
    meta: {
      roleIds: [999],
      title: '异常公示管理' + titleName,
      active: '/index/except-notice'
    },
    component: resolve => require(['@/pages/portal-manage/except-notice'], resolve)
  },
  {
    path: '/index/dealdetails/:objectId',
    name: 'dealdetails',
    meta: {
      roleIds: [999],
      title: '成交公示详情' + titleName,
      active: '/index/deal-notice'
    },
    component: resolve => require(['@/pages/portal-manage/dealdetails'], resolve)
  },
  {
    path: '/index/exceptdetails/:objectId',
    name: 'exceptdetails',
    meta: {
      roleIds: [999],
      title: '异常公示详情' + titleName,
      active: '/index/except-notice'
    },
    component: resolve => require(['@/pages/portal-manage/exceptdetails'], resolve)
  },
  {
    path: '/index/policy-law',
    name: 'policy-law',
    meta: {
      roleIds: [999],
      title: '政策法规管理' + titleName,
      active: '/index/policy-law'
    },
    component: resolve => require(['@/pages/portal-manage/policy-law'], resolve)
  },
  {
    path: '/index/advert',
    name: 'advert',
    meta: {
      roleIds: [999],
      title: '广告管理' + titleName,
      active: '/index/advert'
    },
    component: resolve => require(['@/pages/portal-manage/advert'], resolve)
  },
  {
    path: '/index/credit-eval',
    name: 'credit-eval',
    meta: {
      roleIds: [999],
      title: '信用评价' + titleName,
      active: '/index/credit-eval'
    },
    component: resolve => require(['@/pages/portal-manage/credit-eval'], resolve)
  }
]
