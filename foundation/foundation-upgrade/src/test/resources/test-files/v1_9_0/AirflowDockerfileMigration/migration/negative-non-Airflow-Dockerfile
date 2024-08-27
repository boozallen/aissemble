# Script for creating policy decision point service image
#
# GENERATED DOCKERFILE - please ***DO*** modify.
#
# Generated from: templates/general-docker/policy.decision.point.docker.file.vm

ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE

FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-policy-decision-point:${VERSION_AISSEMBLE}

USER 1001

COPY ./target/aissemble-security.properties /deployments/krausening/
