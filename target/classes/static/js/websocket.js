function connectWebSocket(url) {
    return new Promise((resolve, reject) => {
        const socket = new SockJS(url);
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, () => resolve(stompClient), reject);
    });
}

function subscribeToTopic(stompClient, topic, callback) {
    stompClient.subscribe(topic, (message) => {
        callback(JSON.parse(message.body));
    });
}

function sendMessage(stompClient, destination, payload) {
    stompClient.send(destination, {}, JSON.stringify(payload));
}