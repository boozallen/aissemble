# syntax=docker/dockerfile:1.3-labs
ARG DOCKER_BASELINE_REPO_ID
ARG VERSION_AISSEMBLE
FROM ${DOCKER_BASELINE_REPO_ID}boozallen/aissemble-quarkus:${VERSION_AISSEMBLE}

LABEL org.opencontainers.image.source="https://github.com/boozallen/aissemble"

USER root
ARG AISSEMBLE_HELM_REPO=oci://ghcr.io/boozallen
ARG VERSION_KUBERNETES=1.28
# This overwrites any existing configuration in /etc/yum.repos.d/kubernetes.repo
# Follow up by installing kubectl and helm
RUN cat <<EOF | tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.28/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.28/rpm/repodata/repomd.xml.key
EOF

RUN microdnf install git tar kubectl && \
    curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash && \
    useradd --home /deployments/ --user-group --shell /usr/sbin/nologin --uid 1001 invocation


USER 1001

COPY target/dockerbuild/*.jar /deployments/