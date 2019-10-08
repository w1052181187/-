import store from '@/store'
import SockJS from 'sockjs-client'
export const WebPushSocket = function () {
  let webPushSocket = {
    // collectionPath: '192.168.1.104:8104',
    collectionPath: process.env.WEBSOCKETROOMURL,
    websocket: null,
    userId: '',
    roomId: '',
    userName: '',
    relatedCode: '',
    heartCheck: null
  }
  let lockReconnect = false // 避免ws重复连接
  /**
   * 设置默认参数
   */
  webPushSocket.setSettings = function (roomId, userId, relatedCode) {
    this.roomId = roomId
    this.userId = userId
    this.relatedCode = relatedCode
  }

  webPushSocket.init = function () {
    connect()
    messageHandler()
    this.heartCheck = heartCheck
  }

  webPushSocket.sendMsg = function (msg) {
    sendMessage(msg)
  }

  webPushSocket.close = function () {
    if (webPushSocket.websocket){
      webPushSocket.websocket.close();
      webPushSocket.websocket = null;
    }
  }

  /**
   * websoket构造
   */
  let SocketAdapter = function (options) {
    if ('WebSocket' in window) {
      // websocket = new ReconnectingWebSocket(options.sokectConnectUrl,null,{debug: true, reconnectInterval: 3000});
      webPushSocket.websocket = new WebSocket(options.sokectConnectUrl)
      // } else if ('MozWebSocket' in window) {
      //   websocket = new MozWebSocket(options.sokectConnectUrl)
    } else {
      webPushSocket.websocket = new SockJS(options.sokectConnectHttp)
    }
  }

  /**
   * websoket链接
   */
  let connect = function () {
    let collectionParams = 'roomId=' + webPushSocket.roomId + '&userId=' + webPushSocket.userId + '&relatedCode=' + webPushSocket.relatedCode
    let protocol = window.location.protocol.startsWith('https') ? 's' : ''
    SocketAdapter({
      sokectConnectUrl: 'ws' + protocol + '://' + webPushSocket.collectionPath + '/websocket/room?' + collectionParams,
      sokectConnectHttp: 'http' + protocol + '://' + webPushSocket.collectionPath + '/websocket/room/sockjs.ws?' + collectionParams
    })
  }

  /**
   * websoket链接成功后的操作
   */
  let messageHandler = function () {
    // 连接成功
    webPushSocket.websocket.onopen = function (event) {
      heartCheck.start() // 心跳检测重置
      console.log('成功' + event)
    }
    // 连接失败
    webPushSocket.websocket.onerror = function (event) {
      reconnect()
      console.log('失败' + event)
    }
    // 连接关闭
    webPushSocket.websocket.onclose = function (event) {
      // reconnect()
      console.log('关闭' + event)
    }
    // 获取消息
    webPushSocket.websocket.onmessage = webPushSocket.websocket.onmessage || function (event) {
      heartCheck.reset() // 拿到任何消息都说明当前连接是正常的
      console.log('收到消息')
      console.log(JSON.parse(event.data))
      store.commit('SET_MESSAGES', JSON.parse(event.data))
    }
  }

  let sendMessage = function(msg){
    webPushSocket.websocket.send(msg)
  }

  function reconnect () {
    if (lockReconnect) return
    lockReconnect = true
    setTimeout(function () { // 没连接上会一直重连，设置延迟避免请求过多
      webPushSocket.init()
      lockReconnect = false
    }, 2000)
  }

  /**
   * 心跳检测
   */
  let heartCheck = {
    // 50秒发一次心跳，nginx默认设置60秒超时时间，这样设置可以保持长时间不断开
    timeout: 50000,
    timeoutObj: null,
    serverTimeoutObj: null,
    reset: function () {
      clearTimeout(this.timeoutObj)
      clearTimeout(this.serverTimeoutObj)
      return this
    },
    start: function () {
      var self = this
      this.timeoutObj = setTimeout(function(){
        // 这里发送一个心跳，后端收到后，返回一个心跳消息，
        // onmessage拿到返回的心跳就说明连接正常
        webPushSocket.websocket.send('ping')
        // console.log('ping!')
        self.serverTimeoutObj = setTimeout(function () { // 如果超过一定时间还没重置，说明后端主动断开了
          // 如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
          webPushSocket.websocket.close()
        }, self.timeout)
      }, this.timeout)
    }
  }
  return webPushSocket
}
