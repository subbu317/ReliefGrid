# REST API (http://localhost:8080)
JSON requests. Public read endpoints: `GET /api/shelters`, `/api/stocks`, `/api/requests`, `/api/volunteers`, `/api/plans`, `/api/assignments`, `/api/audits`, `/api/comparison`. Real-time event stream: `GET /api/events` (`change` SSE events). Health: `GET /actuator/health`.

- `POST /api/requests` — `{"shelterId":1,"category":"WATER","quantity":5,"urgency":4}`.
- `POST /api/sync` — `[{"key":"client-generated-unique-id","request":{"shelterId":1,"category":"WATER","quantity":5,"urgency":4}}]`. Per-item status: `CREATED`, `REPLAY`, `CONFLICT`, `INVALID`, `RETRY`.
- `POST /api/stocks` — `{"shelterId":1,"category":"FOOD","available":10}`.
- `POST /api/volunteers` — `{"displayName":"Volunteer A","shelterId":1,"capacity":10}`.
- `POST /api/plans` — creates pending recommendation and audit entry.
- `POST /api/plans/{id}/approve` — atomic reservation of all plan lines, returns HTTP 409 on stale inventory/demand/capacity.
- `POST /api/plans/{id}/reject` — cancel pending plan.
- `POST /api/assignments/{id}/deliver` — mark reserved unit delivered and release inventory reserved balance (not available stock).

**Demo admin endpoints**: all mutation endpoints except `/api/requests` and `/api/sync` require `X-Admin-Key: local-demo-admin` unless `RELIEFGRID_ADMIN_KEY` overrides it. This is only a local demo guard, **not production authentication**. Resource categories: `WATER`, `FOOD`, `BLANKET`, `HYGIENE`.

Example PowerShell:
```powershell
$headers = @{ 'X-Admin-Key'='local-demo-admin' }
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/plans' -Headers $headers
Invoke-RestMethod -Uri 'http://localhost:8080/api/comparison'
```
