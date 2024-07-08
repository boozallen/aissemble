# Script for creating base AIOps Versioning image
ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE

#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM python:3.11-slim AS habushu_builder
# Poetry and supporting plugin installations
RUN python -m ensurepip --upgrade && \
    pip install poetry && \
    poetry self add poetry-monorepo-dependency-plugin && \
    poetry self add poetry-plugin-bundle

WORKDIR /work-dir
COPY --chown=1001 target/containerize-support ./containerize-support/
RUN find . -type f -name pyproject.toml -exec sed -i 's|develop[[:blank:]]*=[[:blank:]]*true|develop = false|g' {} \;

USER root
WORKDIR /work-dir/containerize-support/foundation/aissemble-foundation-versioning-service
ENV POETRY_CACHE_DIR="/.cache/pypoetry"

# export target project's virtual environment to /opt/venv
RUN --mount=type=cache,target=/.cache/pypoetry/ \
    poetry lock && \
    poetry bundle venv /opt/venv
#HABUSHU_BUILDER_STAGE - HABUSHU GENERATED CODE (END)

FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-fastapi:${VERSION_AISSEMBLE} AS builder

# Download Maven
ARG MAVEN_VERSION=3.9.6
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

COPY ./src/main/resources/model_versioning /app/model_versioning

#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (DO NOT MODIFY)
FROM python:3.11-slim AS final
# Copy venv from builder and activate by adding to the path
COPY --from=habushu_builder --chown=1001 /opt/venv /opt/venv/
ENV PATH="/opt/venv/bin:$PATH"
#HABUSHU_FINAL_STAGE - HABUSHU GENERATED CODE (END)

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

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
ENV API_MAIN=model_versioning.versioning_api
COPY --from=builder /start.sh /start.sh
COPY --from=builder /app /app

CMD ["/start.sh"]
