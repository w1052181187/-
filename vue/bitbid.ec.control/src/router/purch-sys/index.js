import {titleName} from '@/assets/js/common'
export default[
  {
    path: '/index/purchsys-trade-record',
    name: 'purchsys-trade-record',
    meta: {
      roleIds: [999],
      title: '交易记录管理' + titleName,
      active: '/index/purchsys-trade-record'
    },
    component: resolve => require(['@/pages/purch-sys/trade-record-sys'], resolve)
  },
  {
    path: '/index/purchsys-trade-details',
    name: 'purchsys-trade-details',
    meta: {
      roleIds: [999],
      title: '交易记录管理详情' + titleName,
      active: '/index/purchsys-trade-record'
    },
    component: resolve => require(['@/pages/purch-sys/trade-sys-details'], resolve)
  },
  {
    path: '/index/purchsys-order-statis',
    name: 'purchsys-order-statis',
    meta: {
      roleIds: [999],
      title: '平台订单统计' + titleName,
      active: '/index/purchsys-order-statis'
    },
    component: resolve => require(['@/pages/purch-sys/order-statis-sys'], resolve)
  },
  {
    path: '/index/purchsys-complain',
    name: 'purchsys-complain',
    meta: {
      roleIds: [999],
      title: '采购投诉' + titleName,
      active: '/index/purchsys-complain'
    },
    component: resolve => require(['@/pages/purch-sys/purch-complain'], resolve)
  }
]
