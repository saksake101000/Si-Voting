/**
 * SI-VOTING - WebSocket Client Utility
 * Reusable WebSocket connection manager
 */

class WebSocketClient {
    constructor(eventId) {
        this.eventId = eventId;
        this.stompClient = null;
        this.status = 'DISCONNECTED';
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 10;
        this.reconnectDelay = 5000;
        this.onMessageCallback = null;
        this.onConnectCallback = null;
        this.onErrorCallback = null;
    }

    /**
     * Connect to WebSocket server
     */
    connect() {
        if (this.status === 'CONNECTED' || this.status === 'CONNECTING') {
            console.warn('⚠️ WebSocket already connected or connecting');
            return;
        }

        if (!this.eventId) {
            console.error('❌ Cannot connect: Event ID is null');
            return;
        }

        try {
            this.status = 'CONNECTING';
            console.log('🔄 Connecting to WebSocket...');

            // Determine WS endpoint using deployment-time API base if provided.
            // If API base ends with '/api', strip it and append '/ws'. Otherwise append '/ws' to origin.
            let apiBase = null;
            try {
                apiBase = (typeof window !== 'undefined' && window.API_BASE) ? window.API_BASE : null;
                const meta = (typeof document !== 'undefined') ? document.querySelector('meta[name="api-base"]') : null;
                if (!apiBase && meta) apiBase = meta.content;
            } catch (e) {
                apiBase = null;
            }

            let sockUrl = '/ws';
            if (apiBase) {
                try {
                    // Remove trailing /api if present (common pattern)
                    const cleaned = apiBase.replace(/\/api\/?$/, '');
                    const u = new URL(cleaned, window.location.href);
                    sockUrl = u.origin + '/ws';
                } catch (e) {
                    // fallback to relative path
                    sockUrl = '/ws';
                }
            }

            const socket = new SockJS(sockUrl);
            this.stompClient = Stomp.over(socket);
            
            // Disable debug logs
            this.stompClient.debug = null;

            this.stompClient.connect({}, 
                (frame) => this.onConnect(frame),
                (error) => this.onError(error)
            );

        } catch (error) {
            this.status = 'ERROR';
            console.error('❌ Failed to establish WebSocket connection:', error);
            this.scheduleReconnect();
        }
    }

    /**
     * Handle successful connection
     */
    onConnect(frame) {
        this.status = 'CONNECTED';
        this.reconnectAttempts = 0;
        console.log('✅ WebSocket Connected:', frame);

        // Subscribe to event topic
        this.stompClient.subscribe(`/topic/event/${this.eventId}`, (message) => {
            try {
                const voteUpdate = JSON.parse(message.body);
                console.log('📨 Vote update received:', voteUpdate);
                
                if (this.onMessageCallback) {
                    this.onMessageCallback(voteUpdate);
                }
            } catch (error) {
                console.error('❌ Error parsing message:', error);
            }
        });

        console.log(`📡 Subscribed to: /topic/event/${this.eventId}`);

        if (this.onConnectCallback) {
            this.onConnectCallback();
        }
    }

    /**
     * Handle connection error
     */
    onError(error) {
        this.status = 'ERROR';
        console.error('❌ WebSocket error:', error);

        if (this.onErrorCallback) {
            this.onErrorCallback(error);
        }

        this.scheduleReconnect();
    }

    /**
     * Schedule reconnection attempt
     */
    scheduleReconnect() {
        if (this.reconnectAttempts >= this.maxReconnectAttempts) {
            console.error('❌ Max reconnection attempts reached');
            return;
        }

        this.reconnectAttempts++;
        console.log(`🔄 Reconnecting in ${this.reconnectDelay / 1000}s (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
        
        setTimeout(() => {
            if (this.status !== 'CONNECTED') {
                this.connect();
            }
        }, this.reconnectDelay);
    }

    /**
     * Disconnect from WebSocket
     */
    disconnect() {
        if (this.stompClient && this.status === 'CONNECTED') {
            this.stompClient.disconnect(() => {
                this.status = 'DISCONNECTED';
                console.log('🔌 Disconnected from WebSocket');
            });
        }
    }

    /**
     * Send message to server
     */
    send(destination, body) {
        if (this.status !== 'CONNECTED') {
            console.error('❌ Cannot send message: Not connected');
            return false;
        }

        try {
            this.stompClient.send(destination, {}, JSON.stringify(body));
            return true;
        } catch (error) {
            console.error('❌ Error sending message:', error);
            return false;
        }
    }

    /**
     * Set callback for received messages
     */
    onMessage(callback) {
        this.onMessageCallback = callback;
    }

    /**
     * Set callback for connection success
     */
    onConnected(callback) {
        this.onConnectCallback = callback;
    }

    /**
     * Set callback for errors
     */
    onConnectionError(callback) {
        this.onErrorCallback = callback;
    }

    /**
     * Get current connection status
     */
    getStatus() {
        return this.status;
    }

    /**
     * Check if connected
     */
    isConnected() {
        return this.status === 'CONNECTED';
    }
}

/**
 * Initialize WebSocket for event page
 * Usage:
 * const ws = initWebSocket(eventId);
 * ws.onMessage((voteUpdate) => {
 *     console.log('New vote:', voteUpdate);
 * });
 */
function initWebSocket(eventId) {
    const wsClient = new WebSocketClient(eventId);
    
    // Auto-connect
    wsClient.connect();
    
    // Auto-reconnect on disconnect
    setInterval(() => {
        if (!wsClient.isConnected() && wsClient.status !== 'CONNECTING') {
            console.log('💔 Connection lost - Attempting to reconnect...');
            wsClient.connect();
        }
    }, 10000);
    
    // Disconnect on page unload
    window.addEventListener('beforeunload', () => {
        console.log('👋 Page unloading - Disconnecting WebSocket...');
        wsClient.disconnect();
    });
    
    return wsClient;
}

// Export for use in other scripts
window.WebSocketClient = WebSocketClient;
window.initWebSocket = initWebSocket;
