export function isvalidPhone (str) {
  const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
  return reg.test(str)
}
// 身份证号验证
export function isvalidId (str) {
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
  return reg.test(str)
}
export function Zipcode (str) {
  const reg = /^[0-9][0-9]{5}$/
  return reg.test(str)
}
export function checkAge (str) {
  const reg = /^[0-9]*$/
  return reg.test(str)
}
export function Numbertype (str) {
  const reg = /^-?\d+\.?\d{0,10}$/
  return reg.test(str)
}
export function Spacetype (str) {
  const reg = /\s+/g
  return reg.test(str)
}
export function Decimal (str) {
  const reg = /^-?\d+\.?\d{0,4}$/
  return reg.test(str)
}
export function links (str) {
  const reg = /^http(s)?:\/\/([\w-]+\.)+[\w-]+(\/[\w- ./?%&=]*)?$/
  return reg.test(str)
}
export function wholeNumbertype (str) {
  const reg = /^\+?[1-9][0-9]*$/
  return reg.test(str)
}
// 金额判断
export function sumType (str) {
  const reg = /^(([1-9][0-9]*)|(([0]\.\d{1,2}|[1-9][0-9]*\.\d{1,2})))$/
  return reg.test(str)
}
// 固话和手机号一起验证
export function isvalidFixedPhone (str) {
  const reg = /^1[3|4|5|7|8][0-9]\d{8}$|^0\d{2,3}-?\d{7,8}$/
  return reg.test(str)
}

// zhangting_20190307
// 邮箱的通用验证
// 长度不限，可以使用英文（包括大小写）、数字、点号、下划线、减号，首字母必须是字母或数字
export function isvalidEmail (str) {
  const reg = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/
  return reg.test(str)
}

// zhangting_20190318
// 验证百分比: 正则匹配0-100之间保留两位小数
export function isValidPercentage (str) {
  const reg = /^(\d|[1-9]\d|100)(\.\d{1,2})?$/
  return reg.test(str)
}

export function validPhoneUser (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!isvalidFixedPhone(value)) {
    callback(new Error('请输入正确的11位手机号码或带区号的固话'))
  } else {
    callback()
  }
}

export function validEmail (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!isvalidEmail(value)) {
    callback(new Error('电子邮箱有误，请重新录入！'))
  } else {
    callback()
  }
}

export function validNumber (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!sumType(value)) {
    callback(new Error('金额有误，请重新录入！'))
  } else {
    callback()
  }
}
