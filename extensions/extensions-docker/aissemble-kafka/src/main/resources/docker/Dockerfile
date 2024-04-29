#Script for creating base Kafka Docker image
FROM wurstmeister/kafka:2.13-2.7.2

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

RUN apt-get update -y \
    && apt-get install unzip \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

RUN adduser --system --shell /bin/bash --no-create-home --uid 1001 --ingroup root --disabled-login kafka && \
chown -R kafka:root /opt/kafka && \
chown -hLR kafka:root /opt/kafka_2.13-2.7.2 && \
chown -R kafka:root /usr/bin/*.sh

RUN mkdir /opt/connectors && \
    chown -R kafka:root /opt/connectors

USER kafka
# Install kafka-connect-spooldir plugin that can be used with Kafka Connect to read CSV data into topics
RUN wget https://d1i4a15mxbxib1.cloudfront.net/api/plugins/jcustenborder/kafka-connect-spooldir/versions/2.0.62/jcustenborder-kafka-connect-spooldir-2.0.62.zip \
    -P /opt/connectors
RUN unzip /opt/connectors/jcustenborder-kafka-connect-spooldir-2.0.62.zip -d /opt/connectors

# Copy over configs
COPY ./src/main/resources/config/ /opt/kafka/config/

# Base image creates a volume mount point at /kafka so any changes to that directory do not persist.
# Because volumes can only be mounted as root, we can't use that directory for our logs and run the container
# as a non-root user. In scenarios where persistence is important, this log should be configured to write
# to an external source or a persistent volume.
ENV KAFKA_LOG_DIRS=/opt/kafka/logs
USER kafka