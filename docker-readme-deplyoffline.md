# Deploying Salty Server without Internet access on server

You do **not** need to copy the Spring Boot source to the target. Build the Docker image on a machine that has internet (or has images cached), save it to a file, then copy that file and the offline Compose file to the target via USB.

## On a machine WITH internet (e.g. your dev machine)

### 1. Build the image

Build for the **architecture of the machine that will run the image**. If it doesn’t match (e.g. you build on Apple Silicon but run on x64), you get “exec format error”.

| Target | Platform | Example |
|--------|----------|---------|
| x64 (Intel/AMD) server | `linux/amd64` | Most PCs, servers, VMs |
| Raspberry Pi 4/5 (64-bit OS) | `linux/arm64` | Pi 4, Pi 5, Pi 400 |
| Apple Silicon (M1/M2/M3) | `linux/arm64` | Same as Pi; native on Mac is default |

**x64 server (e.g. from Apple Silicon):**

```bash
cd /path/to/saltyserver
docker build --platform linux/amd64 -t saltyserver:latest .
```

**Raspberry Pi 4 or 5 (64-bit OS):**

```bash
docker build --platform linux/arm64 -t saltyserver:latest .
```

If your build machine already matches the server (e.g. x64 laptop or a Pi), you can omit `--platform`:

```bash
docker build -t saltyserver:latest .
```

If `docker build --platform` is not available (older Docker), use Buildx and load into the local daemon so you can save:

```bash
docker buildx build --platform linux/amd64 -t saltyserver:latest --load .
# or for Pi:  --platform linux/arm64
```

### 2. Save the image to a file

```bash
docker save -o saltyserver-image.tar saltyserver:latest
```

This creates `saltyserver-image.tar` (may be hundreds of MB; it’s a full image).

### 3. Copy to server

Copy these to remote computer (USB, LAN, etc.), i.e., the Salty Server machine:

| File | Required |
|------|----------|
| `saltyserver-image.tar` | Yes |
| `docker-compose.offline.yml` | Yes |
| `.env` | Optional (override passwords, JWT secret, etc.) |

You do not need to copy source code, `Dockerfile`, or `docker-compose.yml`, or other files.

---

## On the target machine (Salty Server)

Ensure Docker (and Compose v2) are installed, then run:

```bash
docker load -i saltyserver-image.tar
```

You should see something like: `Loaded image: saltyserver:latest`.

### 4. Start the app with Compose

```bash
docker compose -f docker-compose.offline.yml up -d
```

Or use your favorite alternative.

### 5. Use the app

- App: http://localhost:8080 (or the target’s IP/hostname)
- H2 console: http://localhost:8080/h2console (if not disabled in production)
- Default admin: username `admin`, password as set by Compose file or `.env` file.

Data (datbase and images) are stored in a Docker volume and persists across restarts.

---

## Optional: use a different image tag

If you want a versioned tag (e.g. for rollbacks):

```bash
# Build (use --platform linux/amd64 if target server is x64 and you’re on Apple Silicon)
docker build --platform linux/amd64 -t saltyserver:0.0.2 .

# Save
docker save -o saltyserver-0.0.2.tar saltyserver:0.0.2
```

On the target, after copying and loading, either:

- Run: `docker compose -f docker-compose.offline.yml up -d` and change the Compose file to `image: saltyserver:0.0.2`, or  
- Re-tag after load: `docker tag saltyserver:0.0.2 saltyserver:latest` and keep using `docker-compose.offline.yml` as-is.

---

## Multi-architecture images

The base image (Eclipse Temurin) is published for multiple architectures, so you can build this app for **linux/amd64**, **linux/arm64** (Raspberry Pi 4/5, Apple Silicon), and others. There is no single “universal” binary; you choose one platform per build.

- **Offline / save-to-file workflow:** Build once per target. For a Pi, build with `--platform linux/arm64` and save that image; for an x64 server, build with `--platform linux/amd64` and save that image. Each `.tar` is for one architecture.
- **Registry workflow (with internet):** You can publish one tag that works on multiple architectures using a **manifest list**. On a machine with Docker Buildx and a registry (e.g. Docker Hub):

  ```bash
  docker buildx create --use
  docker buildx build --platform linux/amd64,linux/arm64 -t youruser/saltyserver:latest --push .
  ```

  When someone runs `docker pull youruser/saltyserver:latest` or `docker compose pull`, Docker selects the right image for their CPU (x64 or Pi). No need to pick a platform when pulling.

