#!/bin/sh
set -eu

JAVA_OPTS="${JAVA_OPTS:-}"

if [ -n "${DATABASE_MAIN_URL:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.main.url=$DATABASE_MAIN_URL"
fi
if [ -n "${DATABASE_MAIN_USERNAME:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.main.username=$DATABASE_MAIN_USERNAME"
fi
if [ -n "${DATABASE_MAIN_PASSWORD:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.main.password=$DATABASE_MAIN_PASSWORD"
fi
if [ -n "${DATABASE_MAIN_DRIVERCLASSNAME:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.main.driverClassName=$DATABASE_MAIN_DRIVERCLASSNAME"
fi

if [ -n "${DATABASE_RECOMMENDER_ITEM_URL:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.recommender.item.url=$DATABASE_RECOMMENDER_ITEM_URL"
fi
if [ -n "${DATABASE_RECOMMENDER_ITEM_USERNAME:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.recommender.item.username=$DATABASE_RECOMMENDER_ITEM_USERNAME"
fi
if [ -n "${DATABASE_RECOMMENDER_ITEM_PASSWORD:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.recommender.item.password=$DATABASE_RECOMMENDER_ITEM_PASSWORD"
fi

if [ -n "${DATABASE_RECOMMENDER_TAG_URL:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.recommender.tag.url=$DATABASE_RECOMMENDER_TAG_URL"
fi
if [ -n "${DATABASE_RECOMMENDER_TAG_USERNAME:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.recommender.tag.username=$DATABASE_RECOMMENDER_TAG_USERNAME"
fi
if [ -n "${DATABASE_RECOMMENDER_TAG_PASSWORD:-}" ]; then
  JAVA_OPTS="$JAVA_OPTS -Ddatabase.recommender.tag.password=$DATABASE_RECOMMENDER_TAG_PASSWORD"
fi

exec java $JAVA_OPTS -jar /app/app.jar "$@"
