#Script for creating base Kafka Docker image
FROM bitnami/kafka:3.5

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

USER root
RUN apt-get update -y \
    && apt-get install unzip \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

RUN addgroup --gid 1001 kafka && adduser --system --uid 1001 --gid 1001 kafka
RUN mkdir /opt/connectors && \
    chown -R kafka:kafka /opt/connectors && \
    chown -R kafka:kafka /opt/bitnami/kafka

USER kafka

# Copy over configs
COPY ./src/main/resources/config/ /opt/bitnami/kafka/config/
