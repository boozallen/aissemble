# Script for creating base Airflow Docker image
FROM apache/airflow:2.7.0-python3.11

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

USER root

COPY ./target/cacerts/* /usr/local/share/ca-certificates/

RUN update-ca-certificates && apt-get update && apt-get install -y --no-install-recommends apt-utils
RUN apt-get update && apt-get install -y \
  ant \
  fontconfig \
  g++ \
  gcc \
  git \
  libpq-dev \
  openjdk-11-jdk \
  python3-dev \
  unzip \
  && rm -rf /var/lib/apt/lists/* \
  && apt-get clean

# Airflow variables
ARG AIRFLOW_USER_HOME=/opt/airflow
ARG AIRFLOW_VERSION=2.7.0
ARG PYTHON_VERSION=3.11

USER airflow

# Configure Airflow with PostgreSql so we can use LocalExecutor (run more than one DAG at a time)
RUN pip install "apache-airflow[postgres]==${AIRFLOW_VERSION}" \
    --constraint "https://raw.githubusercontent.com/apache/airflow/constraints-${AIRFLOW_VERSION}/constraints-${PYTHON_VERSION}.txt"
RUN pip install --no-cache-dir  psycopg2 \
pip install --no-cache-dir apache-airflow-providers-apache-spark apache-airflow-providers-cncf-kubernetes

USER root

RUN mkdir -p /tmp/mlruns /tmp/model
RUN chown airflow:0 -R /tmp/mlruns /tmp/model

ENV JAVA_HOME /usr/lib/jvm/default-java
RUN export JAVA_HOME

ENV DEBIAN_FRONTEND noninteractive
ENV TERM linux

# Airflow UID is 50000
USER airflow

# Copy custom entrypoint script to start Airflow
ADD ./src/main/resources/scripts/entrypoint.sh ${AIRFLOW_USER_HOME}/entrypoint.sh

ENTRYPOINT ["/opt/airflow/entrypoint.sh"]
