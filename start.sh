#!/bin/sh
set -e

echo "Si-Voting: start.sh invoked"

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT_DIR"

# If docker-compose.yml exists, prefer docker-compose-based start (recommended)
if [ -f docker-compose.yml ]; then
  echo "Found docker-compose.yml — attempting to start services with docker-compose"

  if command -v docker >/dev/null 2>&1 && command -v docker-compose >/dev/null 2>&1; then
    echo "Docker and docker-compose found. Building and starting containers..."
    docker-compose up -d --build
    echo "Containers started. Use 'docker-compose logs -f' to follow logs."
    exit 0
  else
    echo "Warning: docker or docker-compose not available on PATH. Falling back to local Maven build."
  fi
fi

# Fallback: build and run each module locally using Maven and java
if command -v mvn >/dev/null 2>&1; then
  echo "Building serverapp..."
  if [ -d serverapp ]; then
    (cd serverapp && mvn -B -DskipTests package)
  else
    echo "serverapp directory missing"
  fi

  echo "Building clientapp..."
  if [ -d clientapp ]; then
    (cd clientapp && mvn -B -DskipTests package)
  else
    echo "clientapp directory missing"
  fi

  echo "Build finished. You can run the apps manually:" 
  echo "  java -jar serverapp/target/*.jar"
  echo "  java -jar clientapp/target/*.jar (if you run client as Spring Boot)"
  exit 0
else
  echo "Error: neither docker-compose nor mvn is available. Cannot start the app in this environment."
  exit 2
fi
