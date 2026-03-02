#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

CONTAINER_NAME="${BIB_TEST_DB_CONTAINER:-bibsonomy-mariadb-test}"
DB_HOST="${BIB_TEST_DB_HOST:-127.0.0.1}"
DB_PORT="${BIB_TEST_DB_PORT:-3307}"
DB_ROOT_PASSWORD="${BIB_TEST_DB_ROOT_PASSWORD:-root}"
DB_USER="${BIB_TEST_DB_USER:-bibsonomy}"
DB_PASSWORD="${BIB_TEST_DB_PASSWORD:-password}"

echo "Starting fresh MariaDB container '${CONTAINER_NAME}' on ${DB_HOST}:${DB_PORT}..."
docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true
docker run -d \
  --name "${CONTAINER_NAME}" \
  -e MARIADB_ROOT_PASSWORD="${DB_ROOT_PASSWORD}" \
  -e MARIADB_USER="${DB_USER}" \
  -e MARIADB_PASSWORD="${DB_PASSWORD}" \
  -p "${DB_PORT}:3306" \
  mariadb:10.3 >/dev/null

cleanup() {
  echo "Stopping MariaDB container '${CONTAINER_NAME}'..."
  docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true
}
trap cleanup EXIT

echo "Waiting for MariaDB to become available..."
for _ in $(seq 1 120); do
  if docker exec "${CONTAINER_NAME}" mysqladmin ping -uroot "-p${DB_ROOT_PASSWORD}" --silent >/dev/null 2>&1 \
    || docker exec "${CONTAINER_NAME}" mysqladmin ping -uroot --silent >/dev/null 2>&1; then
    break
  fi
  sleep 1
done
docker exec "${CONTAINER_NAME}" mysqladmin ping -uroot "-p${DB_ROOT_PASSWORD}" --silent >/dev/null 2>&1 \
  || docker exec "${CONTAINER_NAME}" mysqladmin ping -uroot --silent >/dev/null

mysql_exec() {
  docker exec -i "${CONTAINER_NAME}" mysql -uroot "-p${DB_ROOT_PASSWORD}" -e "$1" \
    || docker exec -i "${CONTAINER_NAME}" mysql -uroot -e "$1"
}

mysql_exec_optional() {
  if ! mysql_exec "$1"; then
    echo "Warning: optional SQL command failed: $1"
  fi
}

mysql_query() {
  docker exec -i "${CONTAINER_NAME}" mysql -N -uroot "-p${DB_ROOT_PASSWORD}" -e "$1" 2>/dev/null \
    || docker exec -i "${CONTAINER_NAME}" mysql -N -uroot -e "$1" 2>/dev/null
}

echo "Waiting for MariaDB init to finish (final server startup)..."
for _ in $(seq 1 120); do
  if ! docker inspect -f '{{.State.Running}}' "${CONTAINER_NAME}" 2>/dev/null | grep -q true; then
    echo "MariaDB container exited during startup."
    docker logs "${CONTAINER_NAME}" || true
    exit 1
  fi
  if [ "$(mysql_query "SELECT @@port;" || true)" = "3306" ]; then
    break
  fi
  sleep 1
done
if [ "$(mysql_query "SELECT @@port;" || true)" != "3306" ]; then
  echo "MariaDB did not finish startup in time."
  docker logs "${CONTAINER_NAME}" || true
  exit 1
fi

echo "Applying runner-compatible MariaDB settings..."
mysql_exec_optional "SET GLOBAL time_zone = 'Europe/Paris';"
mysql_exec_optional "SET GLOBAL default_storage_engine='InnoDB';"
mysql_exec_optional "SET GLOBAL sql_mode='STRICT_TRANS_TABLES';"
mysql_exec_optional "SET GLOBAL sql_mode=CONCAT(@@global.sql_mode, ',NO_ZERO_DATE');"
mysql_exec_optional "SET GLOBAL sql_mode=CONCAT(@@global.sql_mode, ',NO_AUTO_CREATE_USER');"
mysql_exec_optional "SET GLOBAL sql_mode=CONCAT(@@global.sql_mode, ',NO_ENGINE_SUBSTITUTION');"
mysql_exec_optional "SET GLOBAL sql_mode=CONCAT(@@global.sql_mode, ',NO_ZERO_IN_DATE');"
mysql_exec_optional "SELECT @@SQL_MODE, @@GLOBAL.SQL_MODE;"

echo "Creating fresh test databases..."
mysql_exec "DROP DATABASE IF EXISTS bibsonomy_unit_test;"
mysql_exec "DROP DATABASE IF EXISTS main_db;"
mysql_exec "DROP DATABASE IF EXISTS item_recommender_db;"
mysql_exec "DROP DATABASE IF EXISTS tag_recommender_db;"
mysql_exec "CREATE DATABASE bibsonomy_unit_test;"
mysql_exec "CREATE DATABASE main_db;"
mysql_exec "CREATE DATABASE item_recommender_db;"
mysql_exec "CREATE DATABASE tag_recommender_db;"

echo "Granting privileges..."
mysql_exec "GRANT ALL PRIVILEGES ON main_db.* TO '${DB_USER}'@'%';"
mysql_exec "GRANT ALL PRIVILEGES ON item_recommender_db.* TO '${DB_USER}'@'%';"
mysql_exec "GRANT ALL PRIVILEGES ON tag_recommender_db.* TO '${DB_USER}'@'%';"
mysql_exec "FLUSH PRIVILEGES;"

export BIB_TEST_DB_HOST="${DB_HOST}"
export BIB_TEST_DB_PORT="${DB_PORT}"
export BIB_TEST_DB_USER="${DB_USER}"
export BIB_TEST_DB_PASSWORD="${DB_PASSWORD}"

cd "${ROOT_DIR}"
echo "Running Maven tests with GitLab-style command and settings profile..."
mvn --file pom.xml \
  test org.jacoco:jacoco-maven-plugin:report-aggregate \
  --fail-never \
  -s misc/scripts/settings.xml \
  -P bibsonomy-test-settings
