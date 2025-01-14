/**
 * Created by Administrator on 2019-3-7.
 * 资格预审文件
 */
export default [
  {
    path: '/processManage/projectProcess/prequalification_file/add',
    name: 'projectProcess-prequalificationFile-add',
    meta: {
      title: '流程管理-资格预审文件-添加'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/add'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/update',
    name: 'projectProcess-prequalificationFile-update',
    meta: {
      title: '流程管理-资格预审文件-编辑'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/update'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/detail/:objectId',
    name: 'projectProcess-prequalificationFile-detail',
    meta: {
      title: '流程管理-资格预审文件-查看'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/detail'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/add_doubt',
    name: 'projectProcess-prequalificationFile-add_doubt',
    meta: {
      title: '流程管理-资格预审文件-添加澄清疑义'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/addDoubt'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/update_doubt',
    name: 'projectProcess-prequalificationFile-update_doubt',
    meta: {
      title: '流程管理-资格预审文件-修改澄清疑义'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/updateDoubt'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/doubt_detail/:objectId',
    name: 'projectProcess-prequalificationFile-doubt_detail',
    meta: {
      title: '流程管理-资格预审文件-查看澄清疑义'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/doubtDetail'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/add_clarify',
    name: 'projectProcess-prequalificationFile-add_clarify',
    meta: {
      title: '流程管理-资格预审文件-添加澄清'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/addClarify'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/update_clarify',
    name: 'projectProcess-prequalificationFile-update_clarify',
    meta: {
      title: '流程管理-资格预审文件-修改澄清'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/updateClarify'], resolve)
  },
  {
    path: '/processManage/projectProcess/prequalification_file/clarify_detail/:objectId',
    name: 'projectProcess-prequalificationFile-clarify_detail',
    meta: {
      title: '流程管理-资格预审文件-查看澄清'
    },
    component: resolve => require(['@/pages/processManage/projectProcess/prequalificationFile/clarifyDetail'], resolve)
  }
]
