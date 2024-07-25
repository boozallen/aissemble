FROM registry.access.redhat.com/ubi9/openjdk-11-runtime:1.20 AS builder
USER root
RUN microdnf install -y openssl gzip && \
    curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash

FROM ${docker.baseline.repo.id}/boozallen/aissemble-quarkus:${project.version} AS final

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

COPY --from=builder /usr/local/bin/helm /usr/local/bin/helm
COPY --chown=default target/dockerbuild/*.jar $JAVA_APP_DIR/
