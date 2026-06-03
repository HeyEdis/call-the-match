# Docker VPS Deployment

This setup runs the application with Docker Compose:

- `nginx` exposes the site on port `80`.
- `app` runs the Spring Boot jar on the internal Docker network.
- `mysql` stores the application database in a Docker volume.

## 1. Install Docker on the VPS

```bash
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker $USER
```

Log out and back in after adding your user to the Docker group.

## 2. Clone the project

```bash
git clone https://github.com/HeyEdis/call-the-match.git
cd call-the-match
```

## 3. Optional: set demo passwords

Create a `.env` file if you want to override the default demo passwords:

```bash
MYSQL_PASSWORD=change_this_demo_password
MYSQL_ROOT_PASSWORD=change_this_root_password
```

## 4. Start the stack

```bash
docker compose up -d --build
```

Open the app at:

```text
http://YOUR_VPS_IP
```

## 5. Useful commands

```bash
docker compose ps
docker compose logs -f app
docker compose logs -f nginx
docker compose down
docker compose up -d --build
```

## Demo data

The Compose file starts the app with the `dev` profile, so `InitDataConfig` seeds demo data.

Known demo accounts:

```text
admin@example.com / password
user1@example.com / password
user2@example.com / password
```

The app is configured with `SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop` for a predictable demo reset. Change it to `update` if you want to keep data across application restarts.
