#!/bin/bash

set -e

BOOTSTRAP_SERVER="broker:19092"
KAFKA_TOPICS="/opt/kafka/bin/kafka-topics.sh"

create_topic() {
    local topic=$1
    local partitions=$2

    echo "Creating topic: $topic"

    "$KAFKA_TOPICS" \
        --bootstrap-server "$BOOTSTRAP_SERVER" \
        --create \
        --if-not-exists \
        --topic "$topic" \
        --partitions "$partitions" \
        --replication-factor 1
}

create_topic driver.settlement.completed 3
create_topic delivery.completed 3
create_topic delivery.confirmed 3
create_topic delivery.events 3
create_topic settlement.events 3
create_topic payment.paid 3
create_topic payment.declined 3
create_topic payment.events 3

echo "Kafka topics:"
"$KAFKA_TOPICS"  \
    --bootstrap-server "$BOOTSTRAP_SERVER" \
    --list

echo "Kafka initialization completed."
