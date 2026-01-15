#!/usr/bin/env bash
set -euo pipefail

cd /home/ubuntu/nova

echo "==> Current .env"
cat .env | sed 's/=.*/=*** (hidden)/' || true

echo "==> Pull new image & restart compose"
docker compose --env-file .env -f docker-compose.yml pull
docker compose --env-file .env -f docker-compose.yml up -d --force-recreate --remove-orphans

echo "==> Show running containers"
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"

echo "==> Cleanup dangling images"
docker image prune -f || true