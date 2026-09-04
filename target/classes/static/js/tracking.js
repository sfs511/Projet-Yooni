let ws;
let stompClient;

function connectWebSocket(livraisonId) {
    if (typeof SockJS === 'undefined') {
        console.log('SockJS non disponible, tracking HTTP active');
        return;
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('WebSocket connecte');
        stompClient.subscribe('/topic/tracking/' + livraisonId, (message) => {
            const position = JSON.parse(message.body);
            onTrackingUpdate(position);
        });

        stompClient.subscribe('/topic/notifications', (message) => {
            const notification = JSON.parse(message.body);
            onNotification(notification);
        });
    }, (error) => {
        console.error('Erreur WebSocket:', error);
    });
}

function disconnectWebSocket() {
    if (stompClient) stompClient.disconnect();
}

function onTrackingUpdate(position) {
    if (typeof updateMap === 'function') {
        updateMap(position.latitude, position.longitude, position.livreurNom);
    }
    console.log('Position mise a jour:', position);
}

function onNotification(notification) {
    console.log('Notification recue:', notification);
    if (typeof showNotification === 'function') {
        showNotification(notification);
    }
}
