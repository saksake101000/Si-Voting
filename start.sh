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

# If prebuilt jars exist and java is available, run them directly (useful in environments without mvn/docker)
if command -v java >/dev/null 2>&1; then
  SERVER_JAR="$(ls serverapp/target/*.jar 2>/dev/null | head -n 1 || true)"
  CLIENT_JAR="$(ls clientapp/target/*.jar 2>/dev/null | head -n 1 || true)"

  if [ -n "$SERVER_JAR" ] || [ -n "$CLIENT_JAR" ]; then
    echo "Found runnable jars - will start available services using java"

    if [ -n "$SERVER_JAR" ]; then
      echo "Starting server from $SERVER_JAR"
      nohup java $JAVA_OPTS -jar "$SERVER_JAR" > server.log 2>&1 &
      echo "Server started (logs -> server.log)"
    else
      echo "No server jar found in serverapp/target"
    fi

    if [ -n "$CLIENT_JAR" ]; then
      echo "Starting client app from $CLIENT_JAR"
      nohup java $JAVA_OPTS -jar "$CLIENT_JAR" > client.log 2>&1 &
      echo "Client started (logs -> client.log)"
    else
      echo "No client jar found in clientapp/target"
    fi

    echo "All available jars started. Use 'tail -f server.log' or 'tail -f client.log' to follow logs."
    exit 0
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
