# Script for creating policy decision point service image
#
# GENERATED DOCKERFILE - please ***DO*** modify.
#
# Generated from: ${templateName}

ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE


FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-mlflow:1.7.0

RUN mkdir -p /mlflow/startup
COPY ./src/main/resources/start.sh /mlflow/startup/