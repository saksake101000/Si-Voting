Production deployment notes
==========================

Goal
----
Deploy `clientapp` (static Thymeleaf-rendered pages or static build) and `serverapp` (Spring Boot) separately. The client is served by Nginx; the backend runs as a Java service behind the same domain and provides `/api` and `/ws` (WebSocket) endpoints.

Key ideas
---------
- The client reads the backend base URL at runtime from a meta tag (`<meta name="api-base" content="https://api.example.com/api">`) or from `window.API_BASE` if injected in HTML.
- Nginx serves static files and reverse-proxies `/api/` and `/ws/` to the backend. WebSocket proxying is enabled.
- Keep security-sensitive logic and authoritative aggregation on the backend (serverapp).

What I changed in the repo
-------------------------
- `clientapp/src/main/resources/templates/dashboard/layout.html`
  - Added a configurable `<meta name="api-base">` tag (Thymeleaf will populate if `apiBase` model exists) and included client JS (`/js/api.js`, `/js/utils.js`, `/js/websocket.js`) plus SockJS/STOMP libraries.
- `clientapp/src/main/resources/static/js/api.js`
  - `APIClient` now reads base URL from constructor argument, `window.API_BASE`, or `<meta name="api-base">`, falling back to `http://localhost:8080/api` for local dev.
- `clientapp/src/main/resources/static/js/websocket.js`
  - WebSocket client computes SockJS URL from same deployment-time API base (supports remote backend origin + `/ws`).
- `deploy/nginx-client-proxy.conf` (example) — Nginx configuration to serve client files and proxy API/WebSocket requests.

How to configure the client at deploy time
------------------------------------------
Option A — Inject meta tag into the served HTML
- In your static `index.html` (or in the server-side template if you still use Thymeleaf for production), set:
  <meta name="api-base" content="https://api.example.com/api">
  This is picked up automatically by the client JS.

Option B — Inline global variable
- Before loading `api.js`, add:
  <script>window.API_BASE = "https://api.example.com/api";</script>

Either approach avoids rebuilding the client when you change backend URL.

Nginx sample (explainers)
-------------------------
- `root /var/www/clientapp/public;` : your static client files (index.html, /js, /css, /assets)
- `location /api/` : proxies REST requests to backend at `/api/`
- `location /ws/` : proxies SockJS/STOMP WebSocket handshake and frames (note: proxy_http_version 1.1 and Upgrade/Connection headers)
- Add SSL certs, strict CSP and other headers as needed.

Running the backend
-------------------
You can run the Spring Boot `serverapp` as a standalone jar, systemd service, or under Passenger. Example systemd unit:

[Unit]
Description=SI-VOTING backend
After=network.target

[Service]
User=www-data
WorkingDirectory=/opt/sivoting/serverapp
ExecStart=/usr/bin/java -jar /opt/sivoting/serverapp/sivoting.jar
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure

[Install]
WantedBy=multi-user.target

If you must use Passenger with Nginx, ensure Passenger spawns the JVM correctly and binds to a port that Nginx can proxy to. The Passenger `app_start_command` example you provided must call the correct jar or main class, for example:

app_start_command: env PORT=$PORT java -jar /path/to/sivoting.jar

Notes & checklist before production
-----------------------------------
- [ ] Ensure `serverapp` uses secure production properties (disable debug, set correct DB credentials, connection pool sizing, set `server.forward-headers-strategy=framework` if behind proxy).
- [ ] Enable HTTPS and HSTS on Nginx.
- [ ] Configure CORS on backend if client is served from a different origin (allow only your client origin, enable credentials only if needed).
- [ ] Verify WebSocket endpoint (`/ws`) works through the proxy (test with `wscat` or browser devtools).
- [ ] Set up logging rotation and monitoring (systemd + journald or GC logs, app metrics).
- [ ] Harden security headers and CSP.

If you want, I can:
- Insert a default `<meta name="api-base">` into the main index/html for static builds (or add a small replacement script),
- Create a `systemd` unit or Dockerfile for building and running `serverapp`.
- Provide a one-click deploy script to package client static files into `/var/www/clientapp/public`.

Tell me which of those you'd like next and provide your backend host/domain and whether client and backend will share the same domain (recommended) or be cross-origin, and I'll produce the exact configs and scripts.
