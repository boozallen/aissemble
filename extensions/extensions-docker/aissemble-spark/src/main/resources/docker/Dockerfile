# Script for creating base Spark Docker image
#
ARG SPARK_VERSION
FROM apache/spark-py:v${SPARK_VERSION}

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

USER root

COPY ./target/cacerts/* /usr/local/share/ca-certificates/

# Configures the desired version of Python to install
ARG PYTHON_VERSION=3.11.4

# Setup Spark home directory
RUN mkdir ${SPARK_HOME}/checkpoint && \
  mkdir ${SPARK_HOME}/krausening && \
  mkdir ${SPARK_HOME}/warehouse && \
  mkdir ${SPARK_HOME}/policies && \
  mkdir ${SPARK_HOME}/logs && \
  useradd --home /opt/spark --group 0 --shell /usr/sbin/nologin --uid 185 spark && \
  chown -R spark:spark /opt/spark && \
  ln -s /usr/bin/python3 /usr/bin/python

# git and build-essential are used during pip install of some dependencies (e.g. python-deequ)
# software-properties-common and *-dev dependencies are needed to build an updated version of Python
# as Python 3.11.4 is packaged by default with the base spark-py image
RUN update-ca-certificates && apt-get update -y && apt-get install --assume-yes \
  build-essential \
  curl \
  software-properties-common \
  libnss3-dev \
  zlib1g-dev \
  libgdbm-dev \
  libncurses5-dev \
  libssl-dev \
  libffi-dev \
  libreadline-dev \
  libsqlite3-dev \
  libbz2-dev \
#Patch for CVE-2023-4863: upgrade libwebp7 to latest
  && apt-get upgrade -y libwebp7 \
  && rm -rf /var/lib/apt/lists/* \
  && apt-get clean

# Install newer version of python
RUN curl -L https://www.python.org/ftp/python/${PYTHON_VERSION}/Python-${PYTHON_VERSION}.tgz \
    -o /tmp/Python-${PYTHON_VERSION}.tgz && \
  tar xvf /tmp/Python-${PYTHON_VERSION}.tgz -C /tmp && \
  cd /tmp/Python-${PYTHON_VERSION} && \
  ./configure --enable-optimizations && \
  make install && \
  ln -sf /usr/local/bin/python3 /usr/bin/python

## Add spark configurations
COPY ./src/main/resources/conf/ ${SPARK_HOME}/conf/

RUN chown -R spark:spark ${SPARK_HOME}/conf/

# Fixed the Reflection API breaks java module boundary issue for Java 16+
ENV JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED'

USER spark
