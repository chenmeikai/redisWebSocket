var ws;//websocket实例
var lockReconnect = false;//避免重复连接
var wsUrl = 'ws://localhost:8080/webSocket/heart';

function createWebSocket(url) {
    try {
        ws = new WebSocket(url);
        initEventHandle();
    } catch (e) {
        reconnect(url);
    }
}

function initEventHandle() {
    ws.onclose = function () {
        console.info("连接已关闭，尝试重新连接");
        reconnect(wsUrl);
    };
    ws.onerror = function () {
        reconnect(wsUrl);
    };
    ws.onopen = function () {

        console.info("webSocket连接成功")

        //心跳检测重置
        heartCheck.reset().start();
    };
    ws.onmessage = function (event) {


        console.info("接收到的消息：" + event.data);
        $("#info").text(event.data)

        //如果获取到消息，心跳检测重置
        //拿到任何消息都说明当前连接是正常的
        heartCheck.reset().start();
    }
}

function reconnect(url) {
    if (lockReconnect) return;
    lockReconnect = true;
    //没连接上会一直重连，设置延迟避免请求过多
    setTimeout(function () {
        createWebSocket(url);
        lockReconnect = false;
    }, 2000);
}


//心跳检测
var heartCheck = {
    timeout: 15000,//15秒
    timeoutObj: null,
    serverTimeoutObj: null,
    reset: function () {
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        return this;
    },
    start: function () {
        var self = this;
        this.timeoutObj = setTimeout(function () {
            //这里发送一个心跳，后端收到后，返回一个心跳消息，
            //onmessage拿到返回的心跳就说明连接正常
            console.info("发送心跳包:alive");
            ws.send("alive");
            self.serverTimeoutObj = setTimeout(function () {//如果超过一定时间还没重置，说明后端主动断开了
                ws.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
            }, self.timeout)
        }, this.timeout)
    }
}

createWebSocket(wsUrl);