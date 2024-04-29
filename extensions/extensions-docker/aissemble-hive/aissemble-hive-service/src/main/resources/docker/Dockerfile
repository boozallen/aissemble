FROM openjdk:11-jre-slim

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

WORKDIR /opt

ENV HADOOP_VERSION=3.3.6
ENV METASTORE_VERSION=3.0.0

ENV HADOOP_HOME=/opt/hadoop-${HADOOP_VERSION}
ENV HIVE_HOME=/opt/apache-hive-metastore-${METASTORE_VERSION}-bin

RUN apt-get update && apt-get install curl -y

RUN curl -L https://dlcdn.apache.org/hive/hive-standalone-metastore-${METASTORE_VERSION}/hive-standalone-metastore-${METASTORE_VERSION}-bin.tar.gz | tar zxf - && \
    curl -L https://dlcdn.apache.org/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz | tar zxf -

RUN curl -L https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.30/mysql-connector-java-8.0.30.jar \
      -o ${HIVE_HOME}/lib/mysql-connector-java-8.0.30.jar && \
    curl -L https://repo1.maven.org/maven2/com/sun/activation/javax.activation/1.2.0/javax.activation-1.2.0.jar \
      -o ${HIVE_HOME}/lib/javax.activation-1.2.0.jar && \
    curl https://github.com/delta-io/connectors/releases/download/v0.6.0/delta-hive-assembly_2.12-0.6.0.jar  \
      -o ${HIVE_HOME}/lib/delta-hive-assembly_2.12-0.6.0.jar

RUN groupadd -r hive --gid=1000 && \
    useradd --home ${HIVE_HOME} -g hive --shell /usr/sbin/nologin --uid 1000 hive && \
    chown hive:hive -R ${HIVE_HOME}

USER hive
EXPOSE 9083

ENTRYPOINT ["/opt/apache-hive-metastore-3.0.0-bin/bin/start-metastore"]
