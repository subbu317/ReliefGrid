# ReliefGrid
**Explainable Fairness-Aware Community Resource Allocation & Offline-Safe Synchronization**

Java 21 / Spring Boot / JPA / Flyway / H2 / optional PostgreSQL / React / Vite / IndexedDB / PWA.

> **Simulation software only.** Synthetic data. Not an official disaster response or emergency dispatch platform.

## Demo functions
- 5 shelters, 20 requests, 10 volunteers, 4 categories with limited synthetic stock seeded automatically on first startup
- Admin proposal/approval workflow and atomic multi-row reservations
- Min-cost-flow optimizer with priority and underserved-area bonuses; FCFS baseline comparison
- Partial fulfilment, carrying-capacity checks, category constraints, explanation and audit history
- Offline-first client outbox, idempotent replay and conflict quarantine
- Real-time SSE events with 12-second refresh fallback, responsive PWA dashboard
- H2 file database, Flyway SQL migration, integration and optimizer tests

## Requirements (Windows 10/11)
Install **JDK 21** (Temurin), **Maven 3.9+**, **Node.js 22 LTS** and **Git**. Restart PowerShell after installs. Check:
```powershell
java -version
mvn -version
node -v
npm -v
git --version
```
If Maven isn't recognized, install with `winget install -e --id Apache.Maven` (where available) or download the Maven binary archive from https://maven.apache.org/download.cgi, extract to `C:\Tools\apache-maven-3.9.x` and add its `bin` folder to the **PATH**.

## Local startup (two terminals)
Extract ZIP into `C:\Users\Asus\Desktop\ReliefGrid` (or any folder).

**Terminal 1 — backend**
```powershell
cd "$env:USERPROFILE\Desktop\ReliefGrid\backend"
mvn clean test
mvn spring-boot:run
```
Wait for `Started ReliefGridApplication`; backend at http://localhost:8080, health at http://localhost:8080/actuator/health. H2 console: http://localhost:8080/h2-console; JDBC URL `jdbc:h2:file:./data/reliefgrid`, username `sa`, blank password.

**Terminal 2 — frontend**
```powershell
cd "$env:USERPROFILE\Desktop\ReliefGrid\frontend"
npm install
npm run dev
```
Open http://localhost:5173. Vite automatically proxies `/api` to port 8080. Use the local demo key `local-demo-admin` (preset in the React app) for admin operations. To stop the servers use `Ctrl+C` in each terminal. You can close terminals after stopping; pushing to GitHub does not keep local servers running.

If PowerShell blocks npm scripts, run `npm.cmd install` and `npm.cmd run dev` instead. If port 8080 is occupied, stop that process before running the backend; the frontend proxy assumes 8080.

## Evaluation
The `SimulationBenchmarkTest` builds a second seeded synthetic 5-shelter / 20-request / 10-volunteer scenario; it runs both algorithms and prints measured unit totals, distance-weighted units, and runtime. The `/api/comparison` endpoint executes both solvers on the *currently outstanding* H2 data and reports high-priority fulfilment (urgency >=4), unmet demand, allocation fairness (standard deviation of shelter fulfilment ratios; lower is better), distance × units (not total vehicle tour distance), reported constraint violations, and runtime. `duplicateAssignments` is reported as zero for single-unique-key pick sets; the database additionally enforces per-plan uniqueness. **Do not claim these numbers as benchmark results until tests have actually run on your machine.** Results depend on workload, CPU, JVM warmup and current database state.

## Optional PostgreSQL
Start your own locally installed PostgreSQL 16+ and create database `reliefgrid`. The same Flyway migration works with PostgreSQL. Set environment variables and activate the profile:
```powershell
$env:DB_USERNAME='postgres'
$env:DB_PASSWORD='your-local-password'
$env:JDBC_DATABASE_URL='jdbc:postgresql://localhost:5432/reliefgrid'
$env:RELIEFGRID_ADMIN_KEY='choose-a-local-secret'
cd "$env:USERPROFILE\Desktop\ReliefGrid\backend"
mvn spring-boot:run '-Dspring-boot.run.profiles=postgres'
```
If you change `RELIEFGRID_ADMIN_KEY` locally, update `adminKey` initialization in `frontend/src/App.jsx` or provide the key through a secure login in a production redesign. **Never push your secret to GitHub.**

## Standalone Java-engine smoke check (no Maven dependencies)
```powershell
cd "$env:USERPROFILE\Desktop\ReliefGrid"
mkdir verification -ErrorAction SilentlyContinue
javac -d verification backend\src\main\java\dev\reliefgrid\model\Category.java backend\src\main\java\dev\reliefgrid\engine\AllocationEngine.java evaluation\StandaloneBenchmark.java
java -cp verification StandaloneBenchmark
```
This runs the real allocation engine; see `docs/EVALUATION.md` for the single-run measurements from the generation environment.

## Tests and release builds
```powershell
cd backend
mvn clean verify
mvn package -DskipTests
cd ..\frontend
npm install
npm run build
```
GitHub Actions runs backend tests and frontend build. `Run `npm install` once online to generate `frontend/package-lock.json`, then commit it and switch CI to `npm ci` for fully locked builds.

## Code layout
```
ReliefGrid/
  backend/
    pom.xml
    src/main/java/dev/reliefgrid/{api,config,engine,model,repo,service}/
    src/main/resources/{application.yml,application-postgres.yml,db/migration/V1__schema.sql}
    src/test/java/dev/reliefgrid/{AllocationEngineTest,ApiIntegrationTest,SimulationBenchmarkTest}.java
  frontend/
    public/{icon.svg,favicon.svg}
    src/{App.jsx,main.jsx,offline.js,style.css}
    index.html, vite.config.js, package.json (package-lock.json generated on npm install)
  docs/{API.md,ARCHITECTURE.md}
  .github/workflows/ci.yml
  .gitignore
```

## Push to GitHub
Create an **empty public repository** at https://github.com/new named **ReliefGrid** under `subbu317`. Leave GitHub's auto-README, license and gitignore options unchecked (this project already supplies them). In PowerShell:
```powershell
cd "$env:USERPROFILE\Desktop\ReliefGrid"
git init
git branch -M main
git add .
git commit -m "Initial commit: ReliefGrid resource coordination system"
git remote add origin https://github.com/subbu317/ReliefGrid.git
git push -u origin main
```
For Git credential prompts, authenticate through Git Credential Manager / browser. Do not put a password into the remote URL. If `origin` already exists, use `git remote set-url origin https://github.com/subbu317/ReliefGrid.git`.

## GitHub description
`Java Spring Boot + React PWA for explainable fairness-aware resource allocation, transactional inventory, volunteer capacity constraints and idempotent offline sync.`

**Topics**: `java`, `spring-boot`, `react`, `min-cost-flow`, `operations-research`, `indexeddb`, `pwa`, `offline-first`, `jpa`, `h2`, `postgresql`, `portfolio-project`.

## Resume bullets (features implemented, not unverified impact claims)
- Implemented Java 21/Spring Boot community-resource allocation APIs with JPA persistence, Flyway migrations, and transaction-safe inventory and volunteer-capacity reservations.
- Developed a min-cost-flow allocation engine with urgency-sensitive utility, underserved-location fairness bonuses, geographic distance costs, and comparison against FCFS.
- Built a responsive React PWA with IndexedDB request outbox, idempotent synchronization, conflict handling, admin approval, audit trails, and real-time SSE updates.

See [architecture and objective](docs/ARCHITECTURE.md), [API reference](docs/API.md), and [executed Java-engine evaluation](docs/EVALUATION.md).
