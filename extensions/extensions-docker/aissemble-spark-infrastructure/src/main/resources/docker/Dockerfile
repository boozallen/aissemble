ARG VERSION_AISSEMBLE
ARG DOCKER_BASELINE_REPO_ID
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-spark:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

ARG JARS_DIR

ADD ${JARS_DIR}/* ${SPARK_HOME}/jars/

ENV SPARK_NO_DAEMONIZE=true
USER spark
CMD [ "/bin/bash", "-c", "/opt/spark/sbin/start-history-server.sh & /opt/spark/sbin/start-thriftserver.sh" ]
