ARG METASTORE_VERSION
FROM apache/hive:${METASTORE_VERSION} as appsource

FROM eclipse-temurin:17-jre as final

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

WORKDIR /opt

ARG JARS_DIR

ENV HADOOP_HOME=/opt/hadoop
ENV HIVE_HOME=/opt/hive

COPY --from=appsource /opt/hadoop $HADOOP_HOME
COPY --from=appsource /opt/hive $HIVE_HOME

RUN groupadd -r hive --gid=1000 && \
    useradd --home $HIVE_HOME -g hive --shell /usr/sbin/nologin --uid 1000 hive && \
    chown hive:hive -R $HIVE_HOME && \
    ln -s $JAVA_HOME /opt/jre

ADD ${JARS_DIR}/* $HIVE_HOME/lib/

USER hive

ENTRYPOINT ["/opt/hive/bin/hive", "--skiphadoopversion", "--skiphbasecp", "--verbose", "--service", "metastore"]
