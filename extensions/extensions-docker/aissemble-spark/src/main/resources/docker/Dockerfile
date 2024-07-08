# Script for creating base Spark Docker image
#
ARG SPARK_VERSION
FROM apache/spark-py:v${SPARK_VERSION}

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root

# Configures the desired version of Python to install
ARG PYTHON_VERSION=3.11
# Setup Spark home directory
RUN mkdir ${SPARK_HOME}/checkpoint && \
  mkdir ${SPARK_HOME}/krausening && \
  mkdir ${SPARK_HOME}/warehouse && \
  mkdir ${SPARK_HOME}/policies && \
  mkdir ${SPARK_HOME}/logs && \
  useradd --home /opt/spark --group 0 --shell /usr/sbin/nologin --uid 185 spark && \
  chown -R spark:spark /opt/spark

# Update repositories and add necessary ones
RUN apt-get -y update \
    && apt-get install -y software-properties-common \
    && add-apt-repository ppa:deadsnakes/ppa

# git and build-essential are used during pip install of some dependencies (e.g. python-deequ)
RUN apt-get update -y && apt-get install --assume-yes \
  build-essential \
  curl \
  python${PYTHON_VERSION} \
  python${PYTHON_VERSION}-dev \
#TODO is distutils needed?
  python${PYTHON_VERSION}-distutils \
#Patch for CVE-2023-4863: upgrade libwebp7 to latest
  && apt-get upgrade -y libwebp7 \
  && rm -rf /var/lib/apt/lists/* \
  && apt-get clean \
  && ln -s /usr/bin/python${PYTHON_VERSION} /usr/bin/python

# Pyspark uses `python3` to execute pyspark pipelines. This links out latest python install to that command.
RUN ln -sf /usr/bin/python3.11 /usr/bin/python3

## Add spark configurations
COPY ./src/main/resources/conf/ ${SPARK_HOME}/conf/

RUN chown -R spark:spark ${SPARK_HOME}/conf/

# Fixed the Reflection API breaks java module boundary issue for Java 16+
ENV JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED'

USER spark
