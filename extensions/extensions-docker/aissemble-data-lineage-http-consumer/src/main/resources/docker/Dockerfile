ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-quarkus:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

COPY target/quarkus-app/ /deployments/
