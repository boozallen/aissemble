ARG VERSION_AISSEMBLE
ARG DOCKER_BASELINE_REPO_ID
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE}
ARG DELTA_HIVE_CONNECTOR_VERSION

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

RUN curl -L https://github.com/delta-io/connectors/releases/download/v${DELTA_HIVE_CONNECTOR_VERSION}/delta-hive-assembly_2.12-${DELTA_HIVE_CONNECTOR_VERSION}.jar \
          -o "${SPARK_HOME}"/jars/delta-hive-assembly_2.12-${DELTA_HIVE_CONNECTOR_VERSION}.jar
ARG JARS_DIR
ADD ${JARS_DIR}/* ${SPARK_HOME}/jars/

ENV SPARK_NO_DAEMONIZE=true
USER spark
CMD [ "/bin/bash", "-c", "/opt/spark/sbin/start-history-server.sh & /opt/spark/sbin/start-thriftserver.sh" ]
