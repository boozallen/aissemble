ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-quarkus:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root
RUN useradd --home /home/configstore --user-group --shell /usr/sbin/nologin --uid 1001 configstore

USER 1001

COPY target/quarkus-app/ /deployments/