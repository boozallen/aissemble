ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-quarkus:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source = "https://github.com/boozallen/aissemble"

USER root
RUN microdnf install jq
USER 1001

RUN mkdir -p /deployments/krausening/
COPY target/dockerbuild/*.jar /deployments/

COPY ./src/main/resources/truststore/aissemble-secure.jks /deployments/
COPY ./src/main/resources/krausening/base/aissemble-security.properties /deployments/krausening/
COPY ./src/main/resources/authorization/policies/test-policy.xml /deployments/
COPY ./src/main/resources/authorization/attributes/test-attributes.json /deployments/
COPY ./src/main/resources/authorization/pdp.xml /deployments/
