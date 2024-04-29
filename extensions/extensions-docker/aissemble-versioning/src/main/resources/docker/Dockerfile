# Script for creating base AIOps Versioning image

ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-fastapi:${VERSION_AISSEMBLE} AS builder

RUN python -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"

# Download Maven
ARG MAVEN_VERSION=3.9.6
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

COPY ./target/wheels/* /tmp/wheels/
# Re-install any dependencies defined in the base image into the virtual environment, then install new requirements
RUN pip install -r /tmp/requirements.txt && \
    cd /tmp/wheels/; for x in *.whl; do pip install $x --no-cache-dir; done

COPY ./target/versioning_api.py /app/versioning_api.py
COPY ./src/main/resources/model_versioning /app/model_versioning


# Start a new build stage based on a slim Python image, which will have the minimal amount of code, FastAPI
# components, and installed dependencies copied into it from the initial builder image
FROM python:3.11-slim as final

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

WORKDIR /app

# Install Java (needed for Maven)
RUN mkdir -p /usr/share/man/man1
RUN echo "deb http://deb.debian.org/debian bullseye main" >> /etc/apt/sources.list  \
    && apt-get update -y \
    && apt-get install -y openjdk-11-jre-headless \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Copy over Maven
COPY --from=builder /opt/apache-maven-* /opt/apache-maven
ENV PATH="/opt/apache-maven/bin:$PATH"

# NOTE: you will need to override this Maven settings.xml file 
# with one that contains the credentials for the Nexus server 
# you intend to use when using this versioning service and image.
COPY ./src/main/resources/docker/settings.xml /root/.m2/

# Set/move any required environmental variables and key scripts/configs needed by FastAPI from the base image
# into the slimmed down final Python image
ENV API_MAIN versioning_api
ENV PATH="/opt/venv/bin:$PATH"
ENV PYTHONPATH=/app
COPY --from=builder /start.sh /start.sh

# Copy over the virtual environment (and all pip installed packages) from the initial builder stage
COPY --from=builder /opt/venv /opt/venv
COPY --from=builder /app /app

COPY ./target/versioning_api.py /app/versioning_api.py

# current workaround for arm-based machines to have cryptography installed properly for Krausening
RUN /opt/venv/bin/python -m pip install --upgrade pip \
    && /opt/venv/bin/python -m pip uninstall -y cryptography \
    && /opt/venv/bin/python -m pip install cryptography==39.0.2

CMD ["/start.sh"]
