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
# Install kafka-connect-spooldir plugin that can be used with Kafka Connect to read CSV data into topics
RUN curl -slLO https://d1i4a15mxbxib1.cloudfront.net/api/plugins/jcustenborder/kafka-connect-spooldir/versions/2.0.62/jcustenborder-kafka-connect-spooldir-2.0.62.zip \
    --output-dir /opt/connectors && \
    unzip /opt/connectors/jcustenborder-kafka-connect-spooldir-2.0.62.zip -d /opt/connectors && \
    rm /opt/connectors/jcustenborder-kafka-connect-spooldir-2.0.62.zip

# Copy over configs
COPY ./src/main/resources/config/ /opt/bitnami/kafka/config/
